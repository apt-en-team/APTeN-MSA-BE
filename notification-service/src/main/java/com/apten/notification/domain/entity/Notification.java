package com.apten.notification.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.entity.Tsid;
import com.apten.notification.domain.enums.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// notification-service의 알림 집합 루트를 대표하는 최소 엔티티
// 알림 상세 규칙을 넣기 전에도 JPA 기준 엔티티 형태가 보이도록 최소 필드만 둔다
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    // 모든 서비스 엔티티가 같은 PK 규칙을 따르도록 TSID 마커를 함께 둔다
    @Id
    @Tsid
    private Long id;

    // 알림 제목
    @Column(nullable = false)
    private String title;

    // 알림 처리 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;
}
