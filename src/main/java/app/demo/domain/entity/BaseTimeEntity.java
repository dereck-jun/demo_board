package app.demo.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseTimeEntity {

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDateTime;

    @Column
    private LocalDateTime updatedDateTime;

    @Column
    private LocalDateTime deletedDateTime;

    @PrePersist
    public void prePersist() {
        this.createdDateTime = LocalDateTime.now();
        this.updatedDateTime = this.createdDateTime;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDateTime = LocalDateTime.now();
    }

    @PreRemove
    public void preRemove() {
        this.deletedDateTime = LocalDateTime.now();
    }
}
