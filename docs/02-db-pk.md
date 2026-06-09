# TSID를 도입하며 만난 예상치 못한 문제들

---

## 목차
1. [배경 - 메시지 서버의 식별자 전략](#1-배경---메시지-서버의-식별자-전략)
2. [TSID란?](#2-tsid란)
3. [문제 발견 1 - JPA save()의 함정](#3-문제-발견-1---jpa-save의-함정)
4. [문제 발견 2 - JavaScript Number 정밀도](#4-문제-발견-2---javascript-number-정밀도)
5. [TSID의 실제 활용](#5-tsid의-실제-활용)
6. [결론](#6-결론)

---

## 1. 배경 - 메시지 서버의 식별자 전략

### 1.1. 메시지 서버의 요구사항

메시지 앱은 쓰기 빈도가 매우 높고, 서비스가 운영될수록 누적되는 도메인 특성을 가지고 있다.

메신저 서버에서 메시지는 가장 빈번하게 생성되는 데이터이다. 사용자가 메시지를 보낼 때마다 INSERT 쿼리가 발생하고, 메시지 목록을 조회할 때마다 대량의 데이터를 읽어야 한다. 또한 메시지는 시간순으로 정렬되어야 하고, 분산 환경에서도 고유해야 한다.

이러한 요구사항을 정리하면 다음과 같다.
1. **쓰기 성능**: 빈번한 INSERT에서 인덱스 삽입 성능이 중요하다.
2. **시간순 정렬**: 메시지는 페이징을 위해서 생성 시간순으로 정렬되어야 한다.
3. **전역 고유성**: 분산 환경에서 ID 충돌이 없어야 한다.
4. **보안**: ID로 유추하기 어려워야 한다.

이 요구사항을 기준으로 다양한 PK 후보를 검토했다.

### 1.2. PK 후보 검토 - Auto Increment, UUIDv4

**Auto Increment**

가장 단순한 방식이다. DB가 자동으로 순차적인 숫자를 생성하기 때문에 B+Tree 인덱스에 순서대로 삽입되어 쓰기 성능이 우수하다. 하지만 두 가지 문제가 있다.

1. 보안이 취약하다. `id = 1, 2, 3`처럼 순차적으로 예측이 가능해서 ID를 대입해서 다른 리소스를 탐색하는 공격에 취약하다.
2. 식별자 생성이 DB에 의존적이다. 단일 DB 환경에서는 문제가 없지만, 샤딩이나 다중 DB 인스턴스 환경에서는 전역 고유성 보장을 위한 추가 처리가 필요하다.

**UUIDv4**

128비트의 랜덤 값으로 전역 고유성과 보안을 모두 만족한다. 하지만 여기에도 몇 가지 문제가 있다.

1. 랜덤하게 생성되는 특성 때문에 B+Tree 인덱스에 삽입할 때 페이지 분할이 빈번하게 발생한다. 쓰기 작업이 많은 메시지 테이블에서는 성능 저하 요인이 된다.
2. 문자열로 저장 시 36바이트(바이너리로 저장 시 16바이트)로 Auto Increment(8바이트)보다 인덱스 크기가 크다.
3. `5a9b8f2d-8c4e-4b2a-9e1f-3c5d7a9b8f2d`와 같은 형태라 가독성이 매우 떨어진다.

**UUIDv7**

UUID의 성능 문제를 개선하기 위해 등장한 버전이다. 앞부분에 타임스탬프를 포함하여 시간순 정렬이 가능하기 때문에 UUIDv4에 비해 페이지 분할 문제를 크게 줄일 수 있다. 하지만 여전히 문자열로 저장하면 36바이트(바이너리로 저장 시 16바이트)로, 8바이트인 BIGINT에 비해 인덱스 크기가 최소 2배이다.

### 1.3. TSID 선택

PK 후보를 검토하던 도중 TSID(Time-Sorted Unique Identifier)에 대해 알게 되었다.

TSID는 8바이트 BIGINT와 완벽하게 호환되면서도 노드 ID 설정이 올바르게 관리된다는 전제 하에 시간순 정렬과 전역 고유성을 모두 만족한다.

|             | Auto Increment | UUIDv4                | UUID v7                  | TSID                     |
|-------------|----------------|-----------------------|--------------------------|--------------------------|
| 쓰기 성능       | 최상             | 나쁨                    | 좋음                       | 좋음                       |
| 시간순 정렬      | 가능             | 불가능                   | 가능                       | 가능                       |
| 전역 고유성      | 조건부 가능 (단일 DB) | 가능                    | 가능                       | 조건부 가능 (노드 ID 관리 필요)     |
| 크기          | 8바이트           | 16바이트 (VARCHAR 36바이트) | 16바이트 (VARCHAR 36바이트)    | 8바이트                     |
| 보안 (예측 난이도) | 취약 (순차적 추측 가능) | 우수 (완전 랜덤)            | 비교적 우수 (추측 불가, 시간 유추 가능) | 비교적 우수 (추측 불가, 시간 유추 가능) |

UUIDv7과 TSID는 뒷 부분에 붙는 랜덤/시퀀스 비트 덕분에 다음 ID를 예측하는 공격으로부터 안전한다. 하지만, 두 방식 모두 맨 앞자리에 타임스탬프가 위치하고 있어 ID 자체만으로 생성 시간을 정확히 역산할 수 있다는 특징이 있다.

본 프로젝트에서는 일관된 스키마와 생성 최적화를 위해 모든 엔티티의 PK를 TSID로 통일하였다. 주문이나 결제처럼 생성 시간 노출에 민감한 도메인이 존재하지 않고, 주요 엔티티 간의 조인 성능과 외래키(FK) 인덱스를 줄이는 것이 훨씬 이득이라고 판단했다.

이 선택에는 이전 팀 프로젝트의 경험도 영향을 줬다. 당시 분산 트랜잭션 식별자로 UUIDv4를 사용했는데, 이후 인덱스를 공부하면서 랜덤 UUID가 B+Tree에서 페이지 분할을 유발한다는 것을 뒤늦게 알았다. 그 경험이 이번 프로젝트에서 PK 전략을 처음부터 제대로 고민하게 된 계기였다.

---

## 2. TSID란?

### 2.1. UUIDv7 vs TSID

UUIDv7과 TSID는 모두 시간 기반 정렬을 지원한다. 하지만 몇 가지 차이가 있다. 

UUIDv7은 UUID 표준을 따르기 때문에 128비트(16바이트)를 사용한다. DB에서 BINARY(16)으로 저장하거나 VARCHAR(36)으로 저장하는데, BIGINT(8바이트)와 비교하면 인덱스 크기가 2배에서 4.5배까지 차이가 날 수 있다.

TSID는 8바이트로 DB의 BIGINT와 완벽하게 호환된다. 같은 시간 정렬 보장을 유지하면서 인덱스 크기를 절반으로 줄일 수 있다.

### 2.2. TSID 구조

TSID는 64비트를 다음과 같이 구성한다.

```text
| 42비트 타임스탬프 | 10비트 노드 ID | 12비트 시퀀스 |
```
- **타임스탬프**: 밀리초 단위의 Unix 타임스탬프로, 시간순 정렬을 보장한다.
- **노드 ID**: 분산 환경에서 각 서버를 구별하는 식별자로, 전역 고유성을 보장한다. 최대 1024개의 노드를 생성할 수 있다.
- **시퀀스**: 같은 밀리초 내에서 여러 ID가 생성될 때 순서를 보장한다. 밀리초당  최대 4096개의 고유 ID를 생성할 수 있다. 

프로젝트에서는 `hypersistence-tsid` 라이브러리의 `@Tsid` 어노테이션을 사용해 TSID를 생성했다.

### 2.3. TSID 도입 시 주의사항

TSID는 많은 장점을 제공하지만, 전역 고유성을 보장하기 위해서는 앞에서 언급했듯이 노드 ID 설정이 필요하다. 

`hypersistence-tsid` 라이브러리는 노드 ID를 설정하지 않으면 기본적으로 `0 ~ 1023` 사이 무작위 값이 설정된다.

```java
protected Integer getNode() {
    int max = (1 << this.nodeBits) - 1; // nodeBits의 기본값은 10이며, 생성자에서 설정이 가능하다.
    if (this.node == null) {
        if (TSID.Factory.Settings.getNode() != null) {
            this.node = TSID.Factory.Settings.getNode();
        } else { // node가 없는 경우. 즉, `tsid.node` 시스템 프로퍼티가 존재하지 않는 경우를 의미한다.
            this.node = this.random.nextInt() & max; // 0 ~ 1023 사이의 값
        }
    }

    if (this.node < 0 || this.node > max) {
        this.node = Math.floorMod(this.node, max);
    }

    return this.node;
}
```

서버가 단일 인스턴스라면 문제 없지만, 인스턴스가 늘어날수록 노드 ID 충돌 위험이 높아진다. 따라서 반드시 명시적으로 노드 ID를 설정해주어야 한다.

프로젝트에서는 실행 시점에 `TSID_NODE` 환경 변수를 감지해서, 이를 JVM 시스템 프로퍼티에 바인딩하는 방식으로 이 문제를 해결했다.

```java
@Slf4j
@SpringBootApplication
public class MessengerProjectApplication {

    public static void main(String[] args) {

        String nodeId = System.getenv("TSID_NODE"); // 환경 변수로부터 주입

        log.info("TSID 노드 ID: {}", nodeId);

        if (nodeId != null && !nodeId.isEmpty()) { // 환경 변수가 있으면 시스템 프로퍼티로 등록
            System.setProperty("tsid.node", nodeId);
        }
        
        // 환경 변수가 없으면 기본값 (0 ~ 1023 사이 무작위 값) 사용

        SpringApplication.run(MessengerProjectApplication.class, args);
    }
}
```
---

## 3. 문제 발견 1 - JPA save()의 함정

### 3.1. SimpleJpaRepository의 save()

TSID를 도입하고 나서 예상치 못한 문제를 마주쳤다. JPA의 `save()` 메서드가 불필요한 SELECT 쿼리를 발생시키는 것이었다.

`SimpleJpaRepository`의 `save()` 구현을 살펴보면 다음과 같다.

```java
    @Override
    @Transactional
    public <S extends T> S save(S entity) {
    
        Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);
    
        if (entityInformation.isNew(entity)) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }
```

`isNew` 여부에 따라 `persist`와 `merge` 중 하나가 실행된다. 문제는 이 판단 기준이었다.

JPA는 기본적으로 ID의 존재 여부로 `isNew`를 판단한다. ID가 `null`이면 `persist`, ID가 존재하면 `merge`를 실행한다.

`merge`는 DB에 해당 엔티티가 존재하는지 확인하기 위해서 SELECT 쿼리를 먼저 실행한다. 새로운 엔티티임에도 불구하고 불필요한 조회가 발생하는 것이다.


### 3.2. 애플리케이션 레벨 ID 생성의 문제

Auto Increment나 `@GeneratedValue`로 생성된 ID에는 이러한 문제가 발생하지 않는다. 엔티티를 처음 생성할 때 ID가 `null`이기 때문에 JPA가 `isNew = true`로 판단하고 `persist`를 호출하기 때문이다.

하지만 TSID는 애플리케이션 레벨에서 ID를 생성한다. `@Tsid` 어노테이션이 엔티티 생성 시점에 ID를 할당하기 때문에 DB에 처음 저장하는 엔티티임에도 ID가 존재한다. 따라서 JPA는 이를 새로운 엔티티가 아닌 이미 존재하는 엔티티로 간주하고 `merge`를 호출해 불필요한 SELECT 쿼리가 발생한다.

이 문제는 TSID뿐만 아니라 UUID를 비롯해 애플리케이션 레벨에서 ID를 직접 생성하는 모든 경우에 대해 동일하게 발생한다.

### 3.3. Persistable 적용

이 문제는 `Persistable` 인터페이스를 구현하여 해결할 수 있다. `isNew`를 직접 구현해 JPA의 기본 로직을 오버라이딩 하는 방식이다.

* [[BaseEntity.java](../src/main/java/me/splleat/messengerproject/infrastructure/persistence/entity/BaseEntity.java)]
```java
    @MappedSuperclass
    @EntityListeners(AuditingEntityListener.class)
    public abstract class BaseEntity implements Persistable<Long> {
        @Id @Tsid
        @Column(name = "id")
        private Long id;
    
        @Transient
        private boolean isNew = true; // 생성 시 새로운 엔티티로 표시
    
        @Getter
        @CreatedDate
        @Column(name = "created_at")
        private LocalDateTime createdAt;
    
        @LastModifiedDate
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;
    
        @Override
        public @Nullable Long getId() {
            return id;
        }
    
        @Override
        public boolean isNew() {
            return isNew;
        }
    
        @PostPersist
        @PostLoad
        void markNotNew() {
            this.isNew = false; // 저장 또는 조회 후 기존 엔티티로 표시
        }
    }
```

핵심은 오버라이딩된 `isNew()` 메서드이다. JPA는 이 메서드를 사용해 엔티티가 새로 생성되었는지 판단한다. 해당 메서드는 `@Transient`로 선언된 `isNew` 필드로 결정된다.

 - 엔티티가 처음 생성되면 `isNew = true`로 초기화된다.
 - `@PostPersist`: 엔티티가 저장된 직후 `isNew = false`로 전환된다.
 - `@PostLoad`: DB에서 조회된 엔티티는 `isNew = false`로 전환된다.

이렇게 설정하면 `save()` 호출 시 새롭게 생성된 엔티티는 JPA가 올바르게 신규 엔티티로 인식하여 `persist`를 호출한다. 결과적으로 애플리케이션 레벨에서 생성된 ID 때문에 발생하는 불필요한 SELECT 쿼리를 방지할 수 있다.

---

## 4. 문제 발견 2 - JavaScript Number 정밀도

### 4.1. 문제 상황

TSID를 도입하고 프론트엔드에서 백엔드 로직을 테스트하던 도중에 또 다른 문제가 발생했다. 채널을 생성하고 메시지를 생성했는데, 프론트엔드 화면에 나타나지 않는 것이었다. 

처음에는 웹소켓에 문제가 있다고 생각해 디버깅을 시작했다. 하지만 웹소켓에는 문제가 없었고, 생성된 메시지는 모두 H2 콘솔에서 확인할 수 있었다.
그런데 생성된 채널과 메시지의 ID를 확인하니 프론트가 보관하고 있는 ID 값과 차이가 있었다.

원인은 Java의 `long` 타입과 JavaScript `number`의 정밀도 차이 문제였다. 채널 ID가 달라지니 해당 채널의 메시지를 찾지 못하는 문제가 발생했던 것이었다.

### 4.2. Java long vs JavaScript number

Java의 `long`은 64비트 정수로, 최대 값은 `9,223,372,036,854,775,807`이다. 하지만 JavaScript의 `number`는 IEEE 754 배정밀도 부동소수점 방식을 사용해 최대  `2^53-1(9,007,199,254,740,991)`까지밖에 표현할 수 없다.

TSID는 64비트를 모두 사용하기 때문에 생성되는 ID 값이 JavaScript의 `number` 범위를 초과할 수 있다. 이 범위를 초과하는 숫자를 JavaScript가 처리하면 정밀도가 손실된다.

### 4.3. ToStringSerializer 적용

해결 방법은 `long`을 JSON으로 직렬화 할 때 숫자가 아닌 문자열로 변환하는 것이다. 문자열은 JavaScript에서 정밀도 손실 없이 처리할 수 있다.

Jackson의 `ToStringSerializer`를 전역으로 적용해 해당 문제를 해결했다.

* [[JacksonConfig.java](../src/main/java/me/splleat/messengerproject/common/config/JacksonConfig.java)]
```java
    @Configuration(proxyBeanMethods = false)
    public class JacksonConfig {
        @Bean
        JsonMapper jsonMapper() {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
            return JsonMapper.builder()
                    .addModule(new SimpleModule()
                            .addSerializer(Long.class, ToStringSerializer.instance)
                            .addSerializer(Long.TYPE, ToStringSerializer.instance)
                            .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter))
                            .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter)))
                    .findAndAddModules()
                    .build();
        }
    }
```

`Long.class`와 `Long.TYPE` 두 가지를 모두 등록해 각각 래퍼 타입(`Long`)과 기본 타입(`long`)을 처리할 수 있도록 하였다.

이후 프론트엔드 화면에서 해당 채널의 메시지 목록을 모두 확인할 수 있었다.

---

## 5. TSID의 실제 활용

### 5.1. 엔티티 식별자

프로젝트에서는 `Message`, `Channel`, `Group` 등 모든 엔티티의 식별자를 TSID로 통일하였다.

따라서 모든 엔티티가 `BaseEntity`를 상속받아 전역 고유성과 시간순 정렬, 일관성, Persistable 인터페이스 구현을 통한 성능 최적화의 이점을 얻을 수 있었다.

```java
    @Entity
    @Table(name = "messages")
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class Message extends SoftDeletableEntity {
        // ...
    }
```

`SoftDeletableEntity` 역시 `BaseEntity`를 상속받고 있으므로, 모든 엔티티는 별도의 설정 없이도 TSID를 통한 고유 식별과 `isNew` 최적화의 이점을 누릴 수 있다.


### 5.2. 커서 기반 페이징

일반적으로 커서 기반 페이징에서 시간순 정렬을 보장하려면 `(created_at, id)` 복합 인덱스가 필요하다. `created_at`만으로는 동일한 시간에 생성된 레코드의 순서를 보장할 수 없기 때문이다.

TSID는 타임스탬프와 시퀀스가 ID 안에 존재하기 때문에 ID 자체가 고유한 시간순 정렬 기준이 된다. 덕분에 `id` 단일 인덱스만으로 커서 기반 페이징을 구현할 수 있다.

커서 기반 페이징의 상세 구현은 별도 문서에서 다룬다.

---

## 6. 결론

TSID 도입은 단순히 PK 전략을 바꾸는 것이 아니었다. 애플리케이션 레벨에서 ID를 생성하게 되면서 JPA의 엔티티 생명주기와 JavaScript의 숫자 표현 범위에 대한 이해가 필요했고, 예상하지 못했던 문제들도 함께 드러났다.

새로운 기술을 도입하는 것도 마찬가지로 단순히 그 기술을 이해하고 도입하는 것만으로 끝나지 않는다. 이번 프로젝트에서 겪은 `SimpleJpaRepository`의 `save()` 문제와 JavaScript `number` 정밀도 문제를 통해 새로운 기술을 도입할 떄는 기술 자체뿐 아니라 기존 생태계와의 상호작용까지 고려해야 한다는 점을 배울 수 있었다. 