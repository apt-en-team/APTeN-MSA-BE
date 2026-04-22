package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 프로그램 엔티티
// GX 일정과 정원, 운영 상태를 이 테이블이 관리한다
@Entity
@Table(name = "gx_program")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxProgram extends BaseEntity {

    // GX 프로그램 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 시설 ID
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 프로그램명
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 설명
    @Column(name = "description", length = 500)
    private String description;

    // 시작일
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 종료일
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // 시작 시각
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // 종료 시각
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // 운영 요일
    @Column(name = "days_of_week", nullable = false, length = 30)
    private String daysOfWeek;

    // 최대 인원
    @Column(name = "max_count", nullable = false)
    private Integer maxCount;

    // 최소 인원
    @Column(name = "min_count", nullable = false)
    private Integer minCount;

    // 프로그램 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GxProgramStatus status;
}
