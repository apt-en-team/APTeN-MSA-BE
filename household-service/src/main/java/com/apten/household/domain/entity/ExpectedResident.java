package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.ExpectedResidentStatus;
import com.apten.household.domain.enums.HouseholdMemberRole;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자가 등록한 입주민 명부를 저장하는 엔티티이다.
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "expected_resident",
        indexes = {
                @Index(name = "idx_expected_resident_complex_id", columnList = "complex_id"),
                @Index(name = "idx_expected_resident_household_id", columnList = "household_id"),
                @Index(name = "idx_expected_resident_lookup", columnList = "complex_id, building, unit, status"),
                @Index(name = "idx_expected_resident_matched_user_id", columnList = "matched_user_id")
        }
)
public class ExpectedResident extends BaseEntity {

    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    @Column(name = "household_id", nullable = false)
    private Long householdId;

    @Column(name = "building", nullable = false, length = 10)
    private String building;

    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "relationship", length = 30)
    private String relationship;

    @Column(name = "household_role", nullable = false, length = 20)
    private HouseholdMemberRole householdRole;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExpectedResidentStatus status = ExpectedResidentStatus.AVAILABLE;

    @Column(name = "matched_user_id")
    private Long matchedUserId;

    @Column(name = "matched_at")
    private LocalDateTime matchedAt;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    // 명부가 실제 회원가입 사용자와 매칭되면 사용 완료 상태로 변경한다.
    public void markMatched(Long userId) {
        this.status = ExpectedResidentStatus.MATCHED;
        this.matchedUserId = userId;
        this.matchedAt = LocalDateTime.now();
    }
}
