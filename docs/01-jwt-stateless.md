# JWT는 정말로 Stateless한가?

---

## 목차

1. [배경 - JWT를 선택하기까지](#1-배경---jwt를-선택하기까지)
2. [문제 발견 - 로그아웃](#2-문제-발견-로그아웃)
3. [Session vs JWT 제대로 비교해보기](#3-session-vs-jwt-제대로-비교해보기)
4. [결론](#4-결론)
5. [프로젝트에서의 선택](#5-프로젝트에서의-선택)

---

## 1. 배경 - JWT를 선택하기까지

### 1.1. 웹 애플리케이션의 인증 방식

웹 애플리케이션은 HTTP(HyperText Transfer Protocol) 위에서 동작한다. HTTP는 기본적으로 무상태성(Stateless) 프로토콜이다.

**Stateless란?**

서버가 클라이언트의 이전 상태를 전혀 기억하지 않는 방식이다. 이러한 무상태성 때문에 서버는 더 많은 요청을 처리할 수 있고, 또한 클라이언트의 요청이 1번 서버로 가든, 2번 서버로 가든 아무런 상관이 없기 때문에 매우 자유롭게 서버를 확장할 수 있다.

하지만 현대 대부분의 웹 애플리케이션에서는 누가 요청을 했는지 알 필요가 있다. 메신저 서버도 예외는 아니다. 사용자에게 자신이 속한 채널의 메시지를 보여주기 위해서는 반드시 어떤 사용자가 요청을 했는지 알아야 한다. 그렇기에 HTTP의 Stateless 특성 위에서 사용자를 식별하기 위한 별도의 방식이 필요하다.

이를 해결하는 방식으로 잘 알려진 두 가지 방식이 있다. 바로 세션과 JWT다. 일반적으로 세션과 JWT는 개발자들에게 다음과 같이 알려져 있다.

**세션(Session)**

서버가 상태를 저장하는 Stateful 방식으로, 사용자가 늘어날수록 서버 메모리 부담이 커지고, 수평 확장이 어렵다.

**JWT(JSON Web Token)**

서버가 상태를 저장하지 않는 Stateless 방식으로, 토큰 자체에 정보가 담겨 있어 확장이 쉽고, MSA 환경에 적합하다.

### 1.2. 인증 방식 선택 - JWT

메신저 프로젝트에서 JWT를 선택한 이유는 두 가지였다.

1. JWT의 Stateless 방식이 메신저 서버 확장성에 유리하다고 판단했다. 실시간 메시징 서버같은 경우 연결이 많아질수록 수평 확장이 필요한 구조인데, 세션 방식은 서버 간 세션 공유를 위해 Redis를 통한 클러스터링이 필요하다고 알고 있었다.
2. 솔직히 말하면 JWT 방식으로 인증 방식을 직접 구현해 본 경험이 없었다. 이번 프로젝트에서 직접 구현해보고 싶다는 동기가 컸다.

---

## 2. 문제 발견: 로그아웃

### 2.1. JWT의 로그아웃

JWT 방식을 구현하다가 로그아웃 기능 앞에서 막혔다.

세션 방식은 로그아웃이 단순하다. 서버에서 세션 ID를 삭제하면 해당 쿠키는 즉시 무효화된다. 하지만 JWT는 달랐다.

JWT는 토큰의 소유권이 클라이언트에게 있다. 서버는 토큰을 발급할 뿐, 그 이후에는 토큰이 어디 있는지 알 수 없다. 즉, 서버가 명시적으로 특정 토큰을 무효화할 방법이 없다.

그렇다면 JWT 방식에서 어떻게 사용자의 로그아웃 처리를 할 수 있을까? 찾아본 결과 다음과 같은 방법들을 확인할 수 있었다.

1. **짧은 만료 시간**: 토큰의 TTL을 매우 짧게(5 ~ 15분) 설정하고, 별도의 리프레시(Refresh) 토큰으로 갱신하는 방식이다. 토큰의 만료 시간이 매우 짧기 때문에 탈취되어도 금새 만료된다.
2. **Blacklist 방식**: 로그아웃된 토큰을 서버에 저장해두고, 요청마다 해당 목록을 조회한다.
3. **클라이언트 측 삭제**: 서버는 아무것도 하지 않고, 클라이언트 측에서 토큰을 삭제하는 것으로 로그아웃을 처리한다.

3번 방식 같은 경우 보안상 허점이 있고, 1번 방식만으로는 토큰이 탈취되었을 때 해당 토큰이 만료되기 전까지 막을 방법이 없다. 따라서 서버 측에서 제어가 가능한 2번 방식, Blacklist 방식을 도입하기로 결정했다.

### 2.2. 해결 방식: Blacklist

로그아웃 시 해당 액세스 토큰을 Redis에 저장하고, 매 요청마다 필터에서 Blacklist 존재 여부를 조회하는 방식으로 구현했다.

* [[LogoutUseCase.java](../src/main/java/me/splleat/messengerproject/application/auth/LogoutUseCase.java)]
```java
    @UseCase
    @RequiredArgsConstructor
    public class LogoutUseCase {
        private final BlacklistTokenRepository blacklistTokenRepository;
        private final RefreshTokenRepository refreshTokenRepository;
        private final JwtProvider jwtProvider;
    
        public void execute(LogoutCommand command) {
            try {
                removeRefreshToken(command.refreshToken());
            } catch (BusinessException _) {
                // 리프레시 토큰이 만료되었다면, 새로운 액세스 토큰을 발급받지 못하므로 무시
            }
    
            try {
                saveBlackList(command.accessToken());
            } catch (BusinessException _) {
                // 액세스 토큰이 만료되었다면, 블랙 리스트에 넣을 필요가 없으므로 무시
            }
        }
    
        private void saveBlackList(String accessToken) {
            Claims accessTokenClaims = jwtProvider.getClaims(accessToken);
    
            long expiration = jwtProvider.getExpiration(accessTokenClaims);
    
            long now = System.currentTimeMillis();
    
            String jti = jwtProvider.getJti(accessTokenClaims);
            long ttlMillis = expiration - now; // 액세스 토큰의 남은 만료 시간 만큼만 Redis TTL로 설정
    
            if (ttlMillis < 0) {
                return; // 이미 만료된 액세스 토큰의 경우 무시
            }
    
            blacklistTokenRepository.save(jti, ttlMillis);
        }
    
        private void removeRefreshToken(String refreshToken) {
            Claims refreshTokenClaims = jwtProvider.getClaims(refreshToken);
    
            String jti = jwtProvider.getJti(refreshTokenClaims);
    
            refreshTokenRepository.delete(jti);
        }
    }
```

* [[JwtAuthenticationFilter.java](../src/main/java/me/splleat/messengerproject/infrastructure/security/JwtAuthenticationFilter.java)]
```java
    @RequiredArgsConstructor
    public class JwtAuthenticationFilter extends OncePerRequestFilter {
        private final JwtValidator jwtValidator;
    
        // ...
    
        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
            try {
                String token = getJwtToken(request);
                Authentication authentication = jwtValidator.validateAndGetAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (BusinessException e) {
                request.setAttribute("error", e.getErrorCode());
            }
    
            filterChain.doFilter(request, response);
        }
    
        private String getJwtToken(HttpServletRequest request) {
            String authorization = request.getHeader("Authorization");
    
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new BusinessException(ErrorCode.TOKEN_INVALID);
            }
    
            return authorization.substring(7);
        }
    }
```

* [[JwtValidator.java](../src/main/java/me/splleat/messengerproject/infrastructure/security/JwtValidator.java)]
```java
    @Service
    @RequiredArgsConstructor
    public class JwtValidator {
        private final JwtProvider jwtProvider;
        private final BlacklistTokenRepository blacklistTokenRepository;
    
        public Authentication validateAndGetAuthentication(String token) {
            Claims claims = jwtProvider.getClaims(token);
            String jti = jwtProvider.getJti(claims);
    
            if (blacklistTokenRepository.existsByToken(jti)) {
                throw new BusinessException(ErrorCode.TOKEN_INVALID);
            }
    
            long userId = jwtProvider.getUserId(claims);
            boolean isAdmin = jwtProvider.getIsAdmin(claims);
    
            UserPrincipal principal = UserPrincipal.create(userId, isAdmin);
    
            return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        }
    }
```

구현이 생각보다 까다로웠다. 요청에 담긴 JWT를 파싱하기 위한 필터, 인증 실패를 처리하는 EntryPoint, 로그아웃 시 Redis에 저장된 리프레시 토큰을 삭제하고, 액세스 토큰을 Blacklist로 등록하고 TTL 설정이라던가 세션 방식에 비해 고려해야 할 부분이 한두가지가 아니었다.

그런데 코드를 구현하다 보니 이상한 느낌이 들었다.

### 2.3. JWT는 정말로 Stateless한가?

Blacklist를 Redis에 저장하는 순간, 나는 매 요청마다 Redis를 조회하고 있었다. 그러면 결국 서버가 상태를 저장하는 것(Stateful)이 아닌가?

JWT를 선택한 이유 중 하나가 서버에 상태를 저장하지 않아도 된다는 것이었다. 그런데 로그아웃을 제대로 구현하려니 결국 모든 요청에 대해 Redis라는 외부 저장소를 확인해야 하는 절차가 필요했다.

여기서 의문이 들었다. Blacklist 조회를 위해 Redis를 매 요청마다 조회해야 한다면, 그냥 처음부터 Redis 세션 방식을 쓰는 것과 무엇이 다른가? 오히려 JWT는 토큰 크기가 크고, 서명 검증 비용도 있는데, 그렇다면 세션 방식에 비해 더 나은 점이 있는가?

이러한 의문이 JWT의 트레이드오프를 고민하게 되는 계기가 되었다.

---

## 3. Session vs JWT 제대로 비교해보기

### 3.1. 쿠키, 세션, JWT

두 가지 인증 방식을 비교하기 위해서는 먼저 개념을 정확하게 짚고 넘어갈 필요가 있었다.

쿠키는 HTTP의 Stateless 문제를 해결하기 위한 브라우저의 저장 메커니즘이다. 서버가 응답에 `Set-Cookie` 헤더를 포함하면, 브라우저는 이후 요청마다 해당 값을 자동으로 포함해서 보낸다.

세션은 보통 쿠키를 통해 세션 ID를 전달하고, JWT는 쿠키 또는 Authorization 헤더를 통해 전달할 수 있다. 본 프로젝트에서는 Authorization 헤더에 Bearer Token으로 JWT를 전달했다.

|           | 세션                 | JWT                                  |
|-----------|--------------------|--------------------------------------|
| 요청에 담기는 것 | 세션 ID (랜덤 문자열)     | 토큰 (서명된 JSON 페이로드)                   |
| 실제 데이터 위치 | 서버 (DB, Redis 등)   | 토큰 자체 (Client)                       |
| 서버의 역할    | 세션 ID로 서버에서 사용자 조회 | 토큰 변조 여부(서명), 만료 여부, 블랙리스트 검증 (도입 시) | 

### 3.2. 세션

세션 방식에서 쿠키에 담겨 클라이언트와 서버를 오가는 데이터는 다음과 같이 아무런 의미가 없는 단순한 고유 식별자(UUID 등)이다.

```text
5a9b8f2d-8c4e-4b2a-9e1f-3c5d7a9b8f2d
```

클라이언트는 이 문자열이 무엇을 의미하는지 전혀 알 수 없다. 오직 서버만이 이 식별자를 가지고 서버 내부 저장소(Redis, DB 등)에서 사용자 정보를 조회할 수 있다.

### 3.3. JWT

반면 JWT 방식은 요청에 사용자의 정보와 권한을 인코딩한 토큰 자체를 통째로 담아 보낸다. 점(.)을 기준으로 세 부분으로 나뉜 문자열 형태를 띤다.

```text
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4NTEzMDgzNDEwMzY3MjU3MzYiLCJqdGkiOiIwY2MyNDE5NC05ZDMxLTQxZmUtYjAxOC1hYTA3NjFlNTk1NTYiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNzgwODA2ODIxfQ.EDRHrXEGPnscSlH8Ay2B5IRCgcl2rMP0n0Vc3IyiQEk
```

이 문자열은 단순히 Base64 URL로 인코딩되어 있다. 한 가지 주의할 점이 있는데, '인코딩'은 '암호화'가 아니라는 것이다. 따라서 누구나 복호화 키 없이도 토큰의 정보를 확인할 수 있다. 다음은 실제 애플리케이션에서 수신한 디코딩된 토큰의 구조이다.

```text
    {
      "header" : {
        "alg" : "HS256" // 서명에 사용된 알고리즘
      },
      "payload" : {
        "sub" : "851308341036725736",                   // 사용자 식별자 (TSID)
        "jti" : "0cc24194-9d31-41fe-b018-aa0761e59556", // 토큰 고유 ID
        "isAdmin" : false,                              // 권한 정보 (Role)
        "exp" : 1780806821                              // 토큰 만료 시간
      },
      "signature" : "EDRHrXEGPnscSlH8Ay2B5IRCgcl2rMP0n0Vc3IyiQEk"   // 변조 방지용 서명
    }
```

`payload`에 `sub`(PK)와 `isAdmin` (Role)같은 정보가 그대로 담겨 있는 것을 확인할 수 있다. 

### 3.4. 각 방식의 트레이드오프

문서의 도입부에서 세션과 JWT를 이렇게 비교했었다.

**"세션은 서버가 상태를 저장하는 Stateful 방식으로, 사용자가 늘어날수록 서버 메모리 부담이 커지고, 수평 확장이 어렵다."**

**"JWT는 서버가 상태를 저장하지 않는 Stateless 방식으로, 토큰 자체에 정보가 담겨 있어 확장이 쉽고, MSA 환경에 적합하다."**

하지만 직접 JWT 방식을 구현해보니 반드시 그렇지만은 않다는 것을 알게 되었다.

**세션은 수평 확장이 어렵다?**

구현 방식에 따라 맞기도 하고 틀리기도 하다. 세션 데이터를 서버 메모리에 저장하면 수평 확장이 어렵지만, Redis같은 중앙 세션 저장소를 사용하면 여러 서버가 동일한 세션을 공유할 수 있어 수평 확장이 가능하다.

**JWT는 Stateless이다?**

로그아웃이 필요 없거나, 토큰 탈취에 대해 대응하지 않는다면 그럴지도 모른다. 하지만 실제 서비스에서 서버 주도 로그아웃과 토큰 무효화가 필요하다면, 결국 Blacklist 저장을 위한 Redis가 필요해진다. 그렇다면 결국 이 방식도 세션 방식처럼 비슷한 인프라를 요구한다.

이 외에도 일반적으로 JWT의 장점으로 알려진 내용들은 다음과 같다.

**JWT는 페이로드에 사용자 정보를 담아 DB 조회를 줄여준다**

Redis 세션 방식도 세션 생성 시 필요한 정보(권한 등)를 직렬화하여 함께 저장하면 Blacklist JWT와 마찬가지로 요청당 외부 저장소 조회가 필요하다는 점에서는 유사하다.

**API 게이트웨이/MSA 환경에서 인프라 병목을 분산한다**

마찬가지로 JWT에 Blacklist 방식을 도입하는 순간, 게이트웨이에서 매번 Redis 블랙리스트를 확인해야 하므로 인프라 병목은 세션 방식과 동일하게 발생한다.

직접 구현해본 결과 JWT 방식이 세션 방식에 비해 더 복잡했다. 그렇다면 JWT가 세션에 비해 얻을 수 있는 이점은 무엇인가?

|             | 세션 + Redis | JWT + Blacklist (Redis) | JWT       |
|-------------|------------|-------------------------|-----------|
| 정상 인증 요청 처리 | Redis 조회   | JWT 서명 검증 + Redis 조회    | JWT 서명 검증 |
| 상태 저장소      | 필요         | 필요                      | 불필요       |
| 서버 연산       | 세션 조회      | 서명 검증 + Blacklist 조회    | 서명 검증     |
| 즉시 무효화      | 가능         | 가능 (Blacklist 기반)       | 불가능       |
| 구현 복잡도      | 비교적 단순함    | 비교적 복잡함                 | 비교적 단순함   |

이렇게 블랙리스트 로그아웃 방식까지 구현하면 JWT 방식이 세션 방식에 비해 가지는 실질적인 이점이 생각보다 크지 않다는 것을 알 수 있었다.

물론 JWT가 더 유리한 맥락은 분명히 존재할 것이다. 하지만 아직 그런 규모의 프로젝트를 경험해보지 못했기에, 지금 시점에서 그 장점을 직접 체감하기는 어렵다. 이번 프로젝트 수준에서는 Blacklist까지 구현했을 때 세션 방식 대비 실질적인 이점을 찾기 어려웠다.

---

## 4. 결론

JWT는 Stateless를 지향하는 인증 방식이다. 하지만 실제 서비스에서 요구되는 로그아웃과 토큰 무효화를 구현하려면, JWT 기반 인증도 결국 상태 저장소를 필요로 하게 된다.

JWT = Stateless가 완전히 만족하려면 서버가 토큰을 별도로 추적하거나 무효화하지 않아야 한다. 하지만 대부분의 현대 웹 애플리케이션에서는 로그아웃, 강제 로그아웃, 토큰 탈취 대응과 같은 요구사항이 존재하기 때문에 성립하기 어렵다.

그렇다고 JWT가 무조건 나쁜 선택이라고 말하고 싶은 것이 아니다. JWT가 세션 방식에 비해 가지는 이점은 분명히 존재한다. 중요한 것은 "JWT는 현대적이고 확장 가능한 방식"이라는 고정 관념에서 벗어나, 실제 요구사항에 맞는 방식인지 따져볼 필요가 있다는 것이다.

이 프로젝트에서 JWT를 선택한 것은 잘못된 결정이 아니었다. 직접 구현해보지 않았다면 이 트레이드오프를 체감하지 못했을 것이고, 세션과 JWT의 실질적인 차이도 대략적으로만 알고 넘어갔을 것이다. 이번 프로젝트를 통해 얻은 가장 큰 수확은 JWT가 세션을 완전히 대체하는 기술이 아니라, 서로 다른 트레이드오프를 가진 인증 방식이라는 점을 직접 확인한 것이었다.

## 5. 프로젝트에서의 선택

이번 프로젝트에서는 JWT를 계속 사용하되, Access Token은 짧게(30분) 유지하고 Refresh Token과 Blacklist는 Redis에서 관리하는 절충안을 선택했다.

다만, 다음 프로젝트에서 단순한 인증 요구사항만 있다면, 처음부터 Redis 세션 방식도 적극적으로 검토할 것이다.