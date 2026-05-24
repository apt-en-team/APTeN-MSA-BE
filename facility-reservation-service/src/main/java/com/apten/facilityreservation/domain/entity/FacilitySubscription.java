package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대의 시설 이용 구독을 나타내는 엔티티이다.
// FLAT/PER_PERSON 요금제 시설에서 첫 예약 시 자동 생성되며, 입주민이 명시적으로 해지하기 전까지 청구가 유지된다.
@Entity
@Table(
        name = "facility_subscription",
        indexes = {
                @Index(name = "idx_facility_subscription_household_id", columnList = "household_id"),
                @Index(name = "idx_facility_subscription_facility_id", columnList = "facility_id"),
                @Index(name = "idx_facility_subscription_complex_id", columnList = "complex_id"),
                @Index(name = "idx_facility_subscription_status", columnList = "status")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilitySubscription extends BaseEntity {

    // 구독 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // Household Service 원본 세대 ID이다.
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 구독 대상 시설 ID이다.
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 구독 시작일(첫 예약일)이다.
    @Column(name = "subscribed_at", nullable = false)
    private LocalDate subscribedAt;

    // 구독 해지 요청일이다. 해지 전이면 null이다.
    @Column(name = "cancelled_at")
    private LocalDate cancelledAt;

    // 구독 현재 상태이다.
    @Builder.Default
    @Column(name = "status", nullable = false)
    private FacilitySubscriptionStatus status = FacilitySubscriptionStatus.ACTIVE;

    // 구독을 해지 상태로 전환한다.
    public void cancel(LocalDate cancelledAt) {
        this.cancelledAt = cancelledAt;
        this.status = FacilitySubscriptionStatus.CANCELLED;
    }
}
