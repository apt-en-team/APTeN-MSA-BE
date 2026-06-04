package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.ExpectedResidentStatus;
import com.apten.household.domain.enums.HouseholdMemberRole;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "move_in_date", nullable = false)
    private LocalDate moveInDate;

    @Column(name = "relationship", length = 30)
    private String relationship;

    @Column(name = "household_role", nullable = false, length = 20)
    private HouseholdMemberRole householdRole;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 20, columnDefinition = "varchar(20)")
    private ExpectedResidentStatus status = ExpectedResidentStatus.AVAILABLE;

    @Column(name = "matched_user_id")
    private Long matchedUserId;

    @Column(name = "matched_at")
    private LocalDateTime matchedAt;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    // 명부가 실제 회원가입 사용자와 매칭되면 연결 정보만 기록한다.
    public void markMatched(Long userId) {
        this.status = ExpectedResidentStatus.MATCHED;
        this.matchedUserId = userId;
        this.matchedAt = LocalDateTime.now();
    }

    // 관리자 명부 정보를 수정한다.
    public void update(
            Long householdId,
            String building,
            String unit,
            String name,
            String phone,
            LocalDate birthDate,
            LocalDate moveInDate,
            String relationship,
            HouseholdMemberRole householdRole
    ) {
        this.householdId = householdId;
        this.building = building;
        this.unit = unit;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.moveInDate = moveInDate;
        this.relationship = relationship;
        this.householdRole = householdRole;
    }

    // 매칭된 유저가 탈퇴하면 명부를 다시 매칭 가능 상태로 초기화한다.
    public void resetMatch() {
        this.status = ExpectedResidentStatus.AVAILABLE;
        this.matchedUserId = null;
        this.matchedAt = null;
    }

    // 관리자 명부를 자동매칭 대상에서 제외한다.
    public void disable() {
        this.status = ExpectedResidentStatus.DISABLED;
        this.matchedUserId = null;
        this.matchedAt = null;
    }
}
