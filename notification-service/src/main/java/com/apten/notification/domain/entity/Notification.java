package com.apten.notification.domain.entity;

import com.apten.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// notification-service의 알림 집합 루트를 대표하는 최소 엔티티
// 모든 도메인 엔티티가 BaseEntity를 상속한다는 규칙을 실제 구조로 보여준다
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseEntity {

    // 다음 단계에서 TSID 규칙으로 확장할 기본 식별자
    @Id
    private Long id;
}
