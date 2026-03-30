package me.splleat.messengerproject.infrastructure.persistence.entity;

import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class SoftDeletableEntity extends BaseEntity {
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
