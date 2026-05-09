package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.application.model.request.GxProgramPatchReq;
import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 프로그램 엔티티이다.
// GX 일정과 정원, 운영 상태를 이 테이블이 관리한다.
@Entity
@Table(
        name = "gx_program",
        indexes = {
                @Index(name = "idx_gx_program_facility_id", columnList = "facility_id"),
                @Index(name = "idx_gx_program_complex_id", columnList = "complex_id"),
                @Index(name = "idx_gx_program_status", columnList = "status"),
                @Index(name = "idx_gx_program_start_end", columnList = "start_date,end_date")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxProgram extends BaseEntity {

    // GX 프로그램 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 시설 ID이다.
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 프로그램명이다.
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 설명이다.
    @Column(name = "description", length = 500)
    private String description;

    // 시작일이다.
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 종료일이다.
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // 시작 시각이다.
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // 종료 시각이다.
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // 운영 요일이다.
    @Column(name = "days_of_week", nullable = false, length = 30)
    private String daysOfWeek;

    // 최대 인원이다.
    @Column(name = "max_count", nullable = false)
    private Integer maxCount;

    // 최소 인원이다.
    @Builder.Default
    @Column(name = "min_count", nullable = false)
    private Integer minCount = 0;

    // 프로그램 상태이다.
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private GxProgramStatus status = GxProgramStatus.OPEN;

    // GX 프로그램 수정 요청을 엔티티에 반영한다.
    public void apply(GxProgramPatchReq req) {
        if (req.getName() != null) {
            this.name = req.getName();
        }
        if (req.getDescription() != null) {
            this.description = req.getDescription();
        }
        if (req.getStartDate() != null) {
            this.startDate = req.getStartDate();
        }
        if (req.getEndDate() != null) {
            this.endDate = req.getEndDate();
        }
        if (req.getStartTime() != null) {
            this.startTime = req.getStartTime();
        }
        if (req.getEndTime() != null) {
            this.endTime = req.getEndTime();
        }
        if (req.getDaysOfWeek() != null) {
            this.daysOfWeek = req.getDaysOfWeek();
        }
        if (req.getMaxCount() != null) {
            this.maxCount = req.getMaxCount();
        }
        if (req.getMinCount() != null) {
            this.minCount = req.getMinCount();
        }
    }

    // GX 프로그램을 취소 상태로 변경한다.
    public void cancel() {
        this.status = GxProgramStatus.CANCELLED;
    }
}
