# 커서 기반 양방향 페이징 설계

---

## 목차

1. [배경 - 메시지 목록 조회](#1-배경---메시지-목록-조회)
2. [TSID와 커서 페이징](#2-tsid와-커서-페이징)
3. [메시지 서버의 조회 요구사항](#3-메시지-서버의-조회-요구사항)
4. [양방향 커서 페이징 구현](#4-양방향-커서-페이징-구현)
5. [결론](#5-결론)

---

## 1. 배경 - 메시지 목록 조회

### 1.1. 오프셋 페이징의 한계

일반적으로 가장 잘 알려진 페이징 방식은 오프셋 기반 페이징 방식이다. 우리가 흔히 보는 웹사이트 하단의 `[1] [2] [3] [Next]` 번호판 형태의 페이징을 의미한다.

```sql
SELECT * FROM messages
WHERE channel_id = ?
ORDER BY created_at DESC
LIMIT 20 OFFSET 100
```

하지만 오프셋 방식의 페이징에는 두 가지 문제가 있다.
1. 뒷 페이지로 갈수록 서버가 앞의 데이터를 다 읽고 버려야 하므로 성능이 급격히 떨어진다. `OFFSET 100`은 DB가 앞의 100개 행을 읽고 버린 뒤 그 다음 20개를 반환한다는 의미이다. `OFFSET 10000`은 어떨까? 이처럼 메시지처럼 데이터가 많이 쌓이는 테이블에서는 이 문제가 심각해진다.
2. 사용자가 1페이지를 조회하는 사이 새로운 메시지 2개가 추가되면, 2페이지를 조회할 때 1페이지의 메시지 2개가 중복해서 노출되거나 일부 메시지가 누락될 수 있다. 실시간으로 메시지가 추가되는 메신저 환경에서는 치명적이다.

### 1.2. 커서 페이징으로의 전환

커서 기반 페이징은 특정 기준점(커서)을 기준으로 데이터를 가져오는 방식이다. 유튜브나 인스타그램의 무한 스크롤 방식의 페이징을 의미한다. 

```sql
SELECT * FROM messages
WHERE channel_id = ?
AND id < cursorId
ORDER BY id DESC
LIMIT 20
```

오프셋 기반 페이징과 달리 커서 이후의 데이터만 조회하기 때문에 앞의 전체 데이터를 스캔하지 않아도 된다. 따라서 데이터가 많아져도 일정한 성능을 유지할 수 있다. 또한 새로운 데이터가 추가되어도 커서 기준으로 조회하기 때문에 데이터가 누락되거나 중복이 발생하지 않는다.

---

## 2. TSID와 커서 페이징

### 2.1. created_at 대신 TSID를 사용한 이유

커서 기반 페이징에서 시간 순 정렬을 보장하려면 커서 기준이 되는 컬럼이 고유해야 한다. `created_at`만 사용하면 정확히 `2026-01-01 00:00:00.123`에 3개의 메시지가 동시에 생성되었을 때, 어떤 메시지를 기준으로 해야 할 지 알 수가 없다.

일반적인 해결 방법은 `(created_at, id)` 복합 인덱스를 사용하는 것이다. 하지만 TSID는 타임스탬프와 시퀀스가 ID에 포함되어 있기 때문에 ID 자체가 고유한 시간 순 정렬 커서가 될 수 있다.

따라서 `id` 단일 인덱스만으로 커서 기반 페이징을 구현할 수 있다. `created_at` 컬럼을 커서로 사용할 필요가 없어 쿼리와 인덱스 설계가 매우 단순해진다.

### 2.2. TSID 기반 커서 설계

TSID를 커서로 사용하면 다음과 같이 단순한 비교 연산만으로 페이징이 가능하다.

* **이전 메시지 조회**: `id < cursorId`
* **이후 메시지 조회**: `id > cursorId`

---

## 3. 메시지 서버의 조회 요구사항

메신저에서 메시지 목록 조회는 단순히 최신 메시지를 보여주는 것에 그치지 않는다. 사용자의 상황에 따라 세 가지 다른 조회 방식이 필요하다.

### 3.1. 채널 최초 진입

사용자가 채널에 처음 입장하거나, 읽은 메시지가 없는 경우이다. 가장 최신 메시지부터 보여준다.

### 3.2. 이전 / 다음 메시지 조회

사용자가 스크롤을 올려 더 이전 메시지를 보거나, 반대로 최신 메시지 방향으로 스크롤을 내리는 경우다. 현재 화면의 가장 위 또는 아래 메시지의 ID를 커서로 사용해 추가 메시지를 가져온다.

### 3.3. 메신저 고유의 차별화된 요구사항 - 읽던 위치 복원

사용자가 채널을 나갔다가 다시 입장하는 경우다. 마지막으로 읽은 메시지를 커서로 앞뒤 메시지를 함께 가져온다.

---

## 4. 양방향 커서 페이징 구현

API는 두 가지로 분리했다.

1. 채널 입장 API (`ChannelEnterResult`): 최초 진입(`findByNewest`), 재진입(`findByAround`) 시 사용한다. React Query 초기값 설정을 위해 `hasPrev`, `hasNext`, `prevCursorId`, `nextCursorId`, `lastReadMessageId`를 함께 반환한다.

* [[ChannelEnterResult.java](../src/main/java/me/splleat/messengerproject/application/channel/dto/ChannelEnterResult.java)]
    ```java
    public record ChannelEnterResult(
            List<MessageResponse> messages,
            boolean hasPrev,
            boolean hasNext,
            Long prevCursorId,
            Long nextCursorId,
            Long lastReadMessageId
    ) { /* ... */ }
    ```

2. 스크롤 페이징 API (`ChannelMessagePageResult`): 스크롤로 이전 / 다음 메시지를 추가로 가져올 때 사용한다. 요청에 방향 정보를 같이 받기 때문에 방향은 명시하지 않는다.

* [[ChannelMessagePageResult.java](../src/main/java/me/splleat/messengerproject/application/channel/dto/ChannelMessagePageResult.java)]
   ```java
    public record ChannelMessagePageResult(
        List<MessageResponse> messages,
        boolean hasMore,
        Long cursorId
    ) { /* ... */ }
    ```

공통 조회 로직은 `findBy()` 메서드로 추상화하고 각각의 조건에 따라 알맞은 파라미터를 넘겨준다.

* [[MessageQueryRepository.java](../src/main/java/me/splleat/messengerproject/infrastructure/persistence/querydsl/MessageQueryRepository.java)]
```java
    private MessageSlice findBy(long channelId, Predicate predicate, OrderSpecifier<?> orderSpecifier, boolean reverse) {
        QUserProfile userProfile = QUserProfile.userProfile;
        QMessage message = QMessage.message;
    
        List<MessageResponse> content = queryFactory
                .select(Projections.constructor(MessageResponse.class,
                        message.id,
                        message.userId,
                        message.channelId,
                        userProfile.name,
                        userProfile.imageUrl,
                        message.content,
                        message.type,
                        message.parentMessageId,
                        message.createdAt
                ))
                .from(message)
                .leftJoin(userProfile).on(message.userId.eq(userProfile.userId))
                .where(
                        predicate,
                        message.channelId.eq(channelId)
                )
                .orderBy(orderSpecifier)
                .limit((MESSAGE_SIZE + 1)) // hasNext, hasPrev 확인을 위해 1개 더 조회
                .fetch();
    
        boolean hasMore = false;
        if (content.size() > MESSAGE_SIZE) {
            content.remove(MESSAGE_SIZE);
            hasMore = true;
        }
    
        List<MessageResponse> result = reverse ? content.reversed() : content;
    
        return new MessageSlice(result, hasMore);
    }
```

`MESSAGE_SIZE + 1`개를 조회하는 이유는 이전 / 다음 페이지 존재 여부를 확인하기 위해서이다. 21개가 조회되면 이전 / 다음 페이지가 있다는 의미이므로 마지막 1개를 제거하고 `hasMore = true`를 반환한다.

`reverse` 파라미터는 이전 메시지 조회 시 필요하다. DB에서 `id DESC`로 가져오면 최신 메시지가 최상단에 위치하는데, 클라이언트에서는 시간 순(오름차순)으로 표시를 해야 하기 때문에 한 번 뒤집는다.

### 4.1. 최신 메시지 조회

채널 최초 진입 시 커서 없이 가장 최신 메시지를 조회한다.

```java
    public ChannelEnterResult findByNewest(long channelId) {
        MessageSlice newest = findBy(channelId, null, QMessage.message.id.desc(), true);
    
        return ChannelEnterResult.newest(newest);
    }
```

커서 조건(`predicate`)이 `null`이므로 해당 채널의 전체 메시지 중 최신 20개를 가져온다.

### 4.2. 이전 메시지 조회

사용자가 스크롤을 올려 더 이전 메시지를 조회할 때 사용한다. 현재 화면에서 가장 위에 있는 메시지의 ID를 커서로 사용한다.

```java
    public ChannelMessagePageResult findByPrevId(long channelId, long cursorId) {
        MessageSlice prev = findBy(channelId, QMessage.message.id.lt(cursorId), QMessage.message.id.desc(), true);
    
        return ChannelMessagePageResult.prev(prev);
    }
```

`id < cursorId` 조건으로 커서보다 과거 메시지를 가져온다. `DESC`로 조회한 뒤 `reverse`로 뒤집어 시간 순 정렬로 반환한다.

### 4.3. 다음 메시지 조회

사용자가 스크롤을 내려 더 최신 메시지를 조회할 때 사용한다. 현재 화면에서 가장 아래에 있는 메시지의 ID를 커서로 사용한다.

```java
    public ChannelMessagePageResult findByNextId(long channelId, long cursorId) {
        MessageSlice next = findBy(channelId, QMessage.message.id.gt(cursorId), QMessage.message.id.asc(), false);
    
        return ChannelMessagePageResult.next(next);
    }
```

`id > cursorId` 조건으로 커서보다 최신 메시지를 가져온다. `ASC`로 조회하면 이미 시간 순 정렬이므로 `reverse`가 필요 없다.

### 4.4. Around 조회

채널 재입장 시 마지막으로 읽은 메시지 ID(`lastReadMessageId`)를 기준으로 앞뒤 메시지를 함께 조회한다.

```java
    public ChannelEnterResult findByAroundId(long channelId, long lastReadMessageId) {
        MessageSlice prev = findBy(channelId, QMessage.message.id.lt(lastReadMessageId), QMessage.message.id.desc(), true);
        MessageSlice next = findBy(channelId, QMessage.message.id.goe(lastReadMessageId), QMessage.message.id.asc(), false);
    
        return ChannelEnterResult.around(prev, next, lastReadMessageId);
    }
```

`prev`는 기준 메시지보다 과거 20개, `next`는 기준 메시지를 포함한 이후 메시지 20개를 조회한다. `next`에서 `gt` 대신 `goe`를 사용하는 이유는 마지막으로 읽은 메시지(`lastReadMessageId`) 자체도 결과에 포함되어야 하기 때문이다.

두 메시지 슬라이스를 합쳐 React Query의 양방향 무한 스크롤 초기값으로 사용할 수 있도록 `hasPrev`, `hasNext`, `prevCursorId`, `nextCursorId`, `lastReadMessageId`를 함께 반환한다.

---

## 5. 결론

오프셋 기반 페이징은 구현이 단순하지만 대용량 데이터와 실시간 업데이트 환경에서는 적합하지 않다. 메신저처럼 데이터가 빠르게 쌓이고 실시간성이 중요한 서비스에서는 커서 기반 페이징이 필수적이다.

하지만 이번 설계의 핵심은 단순히 오프셋을 커서로 바꾼 것만이 아니다. 메신저 서비스의 특성 상 사용자는 과거 메시지를 탐색하기도 하고, 읽던 위치에서 다시 대화를 이어가기도 한다. 따라서 단방향 무한 스크롤이 아닌 이전 조회, 다음 조회, 마지막 읽은 위치 기준 조회를 모두 지원하는 양방향 커서 페이징 구조가 필요했다.

TSID를 사용함으로써 단일 식별자만으로 정렬 기준과 커서 역할을 동시에 수행할 수 있었고, 복잡한 복합 커서 없이도 안정적인 페이징을 간단하게 구현할 수 있었다.