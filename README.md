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

## 도메인 구조

```text
src/main/java/me/splleat/messengerproject/
├── domain/                    # 도메인 모델 및 도메인 서비스
│   ├── user/                  # 사용자(User), 사용자 프로필(UserProfile)
│   ├── group/                 # 그룹(Group), 그룹 멤버(GroupMember)
│   ├── channel/               # 채널(Channel), 채널 설정(ChannelUserSetting)
│   └── message/               # 메시지(Message), 첨부 파일(Attachment)
│
├── application/               # 비즈니스 유스케이스 레이어
│   ├── auth/                  # 회원가입, 로그인, 로그아웃, 토큰 재발급 유스케이스
│   ├── group/                 # 그룹 생성, 조회, 초대, 탈퇴 유스케이스
│   ├── channel/               # 채널 생성, 조회, 읽음 처리 유스케이스
│   └── message/               # 메시지 송신 유스케이스
│
├── interfaces/                # 외부 클라이언트 진입점
│   ├── rest/                  # REST API 컨트롤러 (인증, 그룹, 채널 등)
│   └── websocket/             # STOMP 기반 실시간 웹소켓 컨트롤러
│
├── infrastructure/            # 인프라 기술 및 프레임워크 연동
│   ├── persistence/           # 영속성 계층
│   │   ├── entity/            # 공통 추상 엔티티 (BaseEntity, SoftDeletableEntity)
│   │   ├── jpa/               # Spring Data JPA
│   │   └── querydsl/          # QueryDSL
│   ├── security/              # Spring Security 및 JWT 필터/토큰 검증
│   └── websocket/             # STOMP 메시지 핸들러 및 웹소켓 세션 인증 리졸버
│
└── common/                    # 프로젝트 전역 공통 설정 및 예외 처리
    ├── annotation/            # 커스텀 어노테이션(@UseCase)
    ├── config/                # Redis, Jackson, Security, WebSocket 등 설정 클래스
    └── exception/             # 커스텀 비즈니스 예외 및 GlobalExceptionHandler, 공통 에러 응답
```

## 프로젝트 문서

1. **[JWT는 정말로 Stateless한가?](docs/01-jwt-stateless.md)**
2. **[TSID를 도입하며 만난 예상치 못한 문제들](docs/02-db-pk.md)**
3. **[커서 기반 양방향 페이징 설계](docs/03-paging-strategy.md)**
