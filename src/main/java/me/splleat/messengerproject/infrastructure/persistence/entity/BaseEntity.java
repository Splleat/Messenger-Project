package me.splleat.messengerproject.infrastructure.persistence.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Persistable<Long> {
    @Id @Tsid
    private Long id;

    @Transient
    private boolean isNew = true;

    @CreatedDate
    private LocalDateTime createdAt;

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
        this.isNew = false;
    }
}
