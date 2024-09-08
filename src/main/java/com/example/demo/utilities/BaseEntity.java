package com.example.demo.utilities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@MappedSuperclass
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class BaseEntity {
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "created_by", updatable = false)
    private String createdBy;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        log.info("Persist entity : " + this.getClass().getSimpleName());
        this.updatedAt = null;
        this.createdBy = getUserName();
    }

    @PreUpdate
    public void preUpdate() {
        log.info("Update entity : " + this.getClass().getSimpleName());
        this.updatedBy = getUserName();
        this.updatedAt = LocalDateTime.now();
    }

    private String getUserName() {
        return "anonymous";
    }
}