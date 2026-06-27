# 프로젝트 개요

실시간 메신저 서비스

---

## 기술 스택

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/SpringBoot-4.0.6-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/SpringDataJPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-7.1-0769AD?style=flat-square&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![Testcontainers](https://img.shields.io/badge/Testcontainers-38A3D8?style=flat-square&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apache-maven&logoColor=white)

---

## 실행 방법

### 사전 요구사항

- Docker / Docker Compose

### 1. 환경 변수 설정

`.env.example`을 복사해 `.env`를 만들고 값을 채운다.

```bash
cp .env.example .env
```

| 변수                        | 설명                  | 예시                                            |
|---------------------------|---------------------|-----------------------------------------------|
| `SECRET_KEY`              | JWT 서명 키 (32자 이상)   | `this-is-a-secret-key-with-at-least-32-chars` |
| `TSID_NODE`               | TSID 노드 ID          | `0` (기본값)                                     |
| `MYSQL_ROOT_PASSWORD`     | MySQL root 비밀번호     | `change-me-root`                              |
| `MYSQL_DATABASE`          | DB 이름               | `my_db` (기본값)                                 |
| `MYSQL_USER`              | DB 사용자              | `user` (기본값)                                  |
| `MYSQL_PASSWORD`          | DB 사용자 비밀번호         | `change-me`                                   |
| `MINIO_ROOT_USER`         | MinIO 액세스 키         | `change-me`                                   |
| `MINIO_ROOT_PASSWORD`     | MinIO 시크릿 키 (8자 이상) | `change-me-8chars`                            |
| `MINIO_BUCKET`            | 첨부파일 버킷             | `messenger` (기본값)                             |
| `MINIO_CORS_ALLOW_ORIGIN` | MinIO CORS 허용 오리진   | `http://localhost:3000` (기본값)                 |
| `CORS_ALLOWED_ORIGINS`    | 애플리케이션 CORS 허용 오리진  | `http://localhost:3000` (기본값)                 |


### 2. 실행

```bash
docker compose up --build
```

- MySQL, Redis, MinIO, 메신저 애플리케이션이 함께 기동된다.
- 애플리케이션은 DB·Redis·MinIO 헬스체크가 통과하고 MinIO 버킷 초기화가 끝난 뒤에 시작된다.
- 기동 후 `http://localhost:8080` 에서 API에 접근할 수 있다. 
- MinIO 콘솔은 `http://localhost:9001`에서 접근할 수 있다.

### 3. 종료

```bash
docker compose down     # 컨테이너 종료
docker compose down -v  # 데이터(MySQL·MinIO 볼륨)까지 삭제
```

프론트엔드는 [Messenger-Front](https://github.com/Splleat/Messenger-Front) 리포지토리에 존재하며, 백엔드(`localhost:8080`) 기동 후 로컬에서 `npm run dev`(`localhost:3000`)로 실행한다.

---

## 도메인 구조

```text
src/main/java/me/splleat/messengerproject/
├── domain/                    # 도메인 모델 및 도메인 서비스
│   ├── user/                  # 사용자(User), 사용자 프로필(UserProfile)
│   ├── space/                 # 스페이스(Space), 스페이스 멤버(SpaceMember), 권한(SpaceRole)
│   ├── channel/               # 채널(Channel), 채널 설정(ChannelUserSetting)
│   └── message/               # 메시지(Message), 첨부 파일(Attachment)
│
├── application/               # 비즈니스 유스케이스 레이어
│   ├── auth/                  # 회원가입, 로그인, 로그아웃, 토큰 재발급 유스케이스
│   ├── space/                 # 스페이스 생성, 조회, 초대, 탈퇴 유스케이스
│   ├── channel/               # 채널 생성, 조회, 읽음 처리 유스케이스
│   └── message/               # 메시지 송신 유스케이스
│
├── interfaces/                # 외부 클라이언트 진입점
│   ├── rest/                  # REST API 컨트롤러 (인증, 스페이스, 채널 등)
│   └── websocket/             # STOMP 기반 실시간 웹소켓 컨트롤러
│
├── infrastructure/            # 인프라 기술 및 프레임워크 연동
│   ├── message/               # 실시간 메시지 전달 및 발행 신뢰성 보장
│   │   ├── outbox/            # 아웃박스 패턴 (메시지 발행 보장)
│   │   ├── publisher/         # Redis Pub/Sub 발행
│   │   ├── subscriber/        # Redis 구독 → STOMP 라우팅
│   │   └── relay/             # 발행 실패 메시지 복구 스케줄러
│   ├── persistence/           # 영속성 계층
│   │   ├── entity/            # 공통 추상 엔티티 (BaseEntity, SoftDeletableEntity)
│   │   ├── jpa/               # Spring Data JPA
│   │   └── querydsl/          # QueryDSL
│   ├── security/              # Spring Security 및 JWT 필터/토큰 검증
│   └── websocket/             # STOMP 메시지 핸들러 및 웹소켓 세션 인증 리졸버
│
└── common/                    # 프로젝트 전역 공통 설정 및 예외 처리
    ├── annotation/            # 커스텀 어노테이션 (@UseCase, @DistributedLock)
    ├── aop/                   # 분산 락 AOP (DistributedLockAspect)
    ├── parser/                # SpEL 기반 동적 Lock Key 파서
    ├── config/                # Redis, Jackson, Security, WebSocket 등 설정 클래스
    └── exception/             # 커스텀 비즈니스 예외 및 GlobalExceptionHandler, 공통 에러 응답
```

## 프로젝트 문서

1. **[JWT는 정말로 Stateless한가?](docs/01-jwt-stateless.md)**
2. **[TSID를 도입하며 만난 예상치 못한 문제들](docs/02-db-pk.md)**
3. **[커서 기반 양방향 페이징 설계](docs/03-paging-strategy.md)**
