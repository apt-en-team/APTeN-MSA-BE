package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.HouseholdMatchProcessType;
import com.apten.household.domain.enums.HouseholdMatchStatus;
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

// 회원가입 시 입력한 세대 매칭 요청을 저장하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "household_match_request",
        indexes = {
                @Index(name = "idx_match_request_user_id", columnList = "user_id"),
                @Index(name = "idx_match_request_complex_id", columnList = "complex_id"),
                @Index(name = "idx_match_request_status", columnList = "match_status"),
                @Index(name = "idx_match_request_process_type", columnList = "process_type")
        }
)
public class HouseholdMatchRequest extends BaseEntity {

    // 매칭 요청 PK
    @Id
    @Tsid
    private Long id;

    // 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 입력 이름
    @Column(name = "input_name", nullable = false, length = 50)
    private String inputName;

    // 입력 연락처
    @Column(name = "input_phone", nullable = false, length = 20)
    private String inputPhone;

    // 입력 생년월일
    @Column(name = "input_birth_date", nullable = false)
    private LocalDate inputBirthDate;

    // 입력 동
    @Column(name = "input_building", nullable = false, length = 10)
    private String inputBuilding;

    // 입력 호
    @Column(name = "input_unit", nullable = false, length = 10)
    private String inputUnit;

    // 매칭된 세대 ID
    @Column(name = "matched_household_id")
    private Long matchedHouseholdId;

    // 처리 방식
    @Enumerated(EnumType.STRING)
    @Column(name = "process_type", nullable = false, length = 20)
    private HouseholdMatchProcessType processType;

    // 처리 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "match_status", nullable = false, length = 20)
    private HouseholdMatchStatus matchStatus;

    // 처리 시각
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    // 매칭 결과를 반영한다
    public void apply(Long matchedHouseholdId, HouseholdMatchProcessType processType, HouseholdMatchStatus matchStatus) {
        this.matchedHouseholdId = matchedHouseholdId;
        this.processType = processType;
        this.matchStatus = matchStatus;
    }

    // 처리 완료 시각을 갱신한다
    public void updateProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
