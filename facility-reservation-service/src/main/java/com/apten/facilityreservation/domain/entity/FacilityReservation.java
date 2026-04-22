package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// facility-reservation-service의 예약 집합 루트를 대표하는 최소 엔티티
// BaseEntity 상속과 공통 시간 필드 사용 규칙을 실제 구조로 보여준다
@Entity
@Getter
@Setter
@NoArgsConstructor
public class FacilityReservation extends BaseEntity {

    // 다음 단계에서 TSID 규칙으로 확장할 기본 식별자
    @Id
    private Long id;
}
