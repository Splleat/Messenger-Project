package me.splleat.messengerproject.infrastructure.message.outbox;

import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;
import org.springframework.util.ClassUtils;
import tools.jackson.databind.json.JsonMapper;

public record DomainCreatedEvent(
        BaseEntity entity,
        String eventType,
        Object payload
) {
    public static DomainCreatedEvent from(BaseEntity entity) {
        return of(entity, entity);
    }

    public static DomainCreatedEvent of(BaseEntity entity, Object payload) {
        String eventType = ClassUtils.getUserClass(entity).getSimpleName() + "Created";

        return new DomainCreatedEvent(entity, eventType, payload);
    }

    public String toJson(JsonMapper jsonMapper) {
        return jsonMapper.writeValueAsString(payload);
    }
}
