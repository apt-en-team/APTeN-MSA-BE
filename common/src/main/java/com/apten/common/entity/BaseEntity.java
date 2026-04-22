package com.apten.common.entity;

import java.time.LocalDateTime;
import lombok.Getter;

// 공통 시간 필드 베이스 클래스
@Getter
public abstract class BaseEntity {

    // 생성 시각
    private LocalDateTime createdAt;

    // 수정 시각
    private LocalDateTime updatedAt;

    // 생성 시각 초기화
    protected void markCreated() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // 수정 시각 갱신
    protected void markUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
}
