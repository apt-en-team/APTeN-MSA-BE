package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.HouseholdStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 상태 변경 이력을 저장하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "household_history",
        indexes = {
                @Index(name = "idx_household_history_household_id", columnList = "household_id"),
                @Index(name = "idx_household_history_changed_at", columnList = "changed_at"),
                @Index(name = "idx_household_history_to_status", columnList = "to_status")
        }
)
public class HouseholdHistory extends BaseEntity {

    // 이력 PK
    @Id
    @Tsid
    private Long id;

    // 세대 ID
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 이전 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 20)
    private HouseholdStatus fromStatus;

    // 변경 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 20)
    private HouseholdStatus toStatus;

    // 변경 사유
    @Column(name = "reason", length = 255)
    private String reason;

    // 상태 변경 시각
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    // 이력 내용을 갱신한다
    public void apply(HouseholdStatus fromStatus, HouseholdStatus toStatus, String reason, LocalDateTime changedAt) {
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.reason = reason;
        this.changedAt = changedAt;
    }
}
