package com.apten.common.entity;

import java.time.LocalDateTime;
import lombok.Getter;

// 여러 서비스의 엔티티가 공통으로 쓰는 생성 시각과 수정 시각 베이스 클래스
// auth-service를 포함한 각 도메인 서비스가 동일한 시간 필드 구조를 재사용할 때 기준이 된다
@Getter
public abstract class BaseEntity {

    // 엔티티가 처음 만들어진 시점
    private LocalDateTime createdAt;

    // 엔티티가 마지막으로 변경된 시점
    private LocalDateTime updatedAt;

    // 새 엔티티를 만들 때 생성 시각과 수정 시각을 함께 맞춘다
    // 서비스 계층이 저장 직전 최소한의 시간 정보를 채울 때 호출할 수 있다
    protected void markCreated() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // 이미 존재하는 엔티티를 갱신할 때 수정 시각만 다시 기록한다
    // JPA Auditing 없이도 공통 시간 갱신 규칙을 유지하기 위한 최소 메서드다
    protected void markUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
}
