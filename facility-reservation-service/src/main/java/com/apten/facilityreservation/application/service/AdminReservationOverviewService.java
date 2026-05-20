package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.request.AdminReservationOverviewReq;
import com.apten.facilityreservation.application.model.response.AdminReservationOverviewRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.domain.entity.HouseholdCache;
import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.domain.entity.UserCache;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import com.apten.facilityreservation.domain.enums.ReservationKind;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.domain.repository.GxProgramRepository;
import com.apten.facilityreservation.domain.repository.GxReservationRepository;
import com.apten.facilityreservation.domain.repository.HouseholdCacheRepository;
import com.apten.facilityreservation.domain.repository.ReservationRepository;
import com.apten.facilityreservation.domain.repository.UserCacheRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 관리자 예약 통합 개요 조회 서비스이다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReservationOverviewService {

    private final ReservationRepository reservationRepository;
    private final GxReservationRepository gxReservationRepository;
    private final GxProgramRepository gxProgramRepository;
    private final FacilityRepository facilityRepository;
    private final UserCacheRepository userCacheRepository;
    private final HouseholdCacheRepository householdCacheRepository;

    public PageResponse<AdminReservationOverviewRes> getOverview(Long complexId, AdminReservationOverviewReq req) {
        ReservationKind kind = req.getReservationKind();
        List<AdminReservationOverviewRes> items = new ArrayList<>();

        if (kind == null || kind == ReservationKind.FACILITY) {
            items.addAll(fetchFacilityItems(complexId, req));
        }

        if (kind == null || kind == ReservationKind.GX) {
            items.addAll(fetchGxItems(complexId, req));
        }

        items.sort(Comparator.comparing(
                AdminReservationOverviewRes::getCreatedAt,
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed());

        // 예약자명 Like 필터
        String residentNameFilter = req.getResidentName();
        if (residentNameFilter != null && !residentNameFilter.isBlank()) {
            String keyword = residentNameFilter.toLowerCase();
            items = items.stream()
                    .filter(item -> item.getResidentName() != null
                            && item.getResidentName().toLowerCase().contains(keyword))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        // 시설명/프로그램명 Like 필터
        String facilityNameFilter = req.getFacilityName();
        if (facilityNameFilter != null && !facilityNameFilter.isBlank()) {
            String keyword = facilityNameFilter.toLowerCase();
            items = items.stream()
                    .filter(item -> {
                        boolean facilityMatch = item.getFacilityName() != null
                                && item.getFacilityName().toLowerCase().contains(keyword);
                        boolean programMatch = item.getProgramName() != null
                                && item.getProgramName().toLowerCase().contains(keyword);
                        return facilityMatch || programMatch;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return buildPageResponse(items, req.getPage(), req.getSize());
    }

    private List<AdminReservationOverviewRes> fetchFacilityItems(Long complexId, AdminReservationOverviewReq req) {
        // status 파라미터를 ReservationStatus로 변환한다. WAITING은 FACILITY에 해당 없으므로 빈 결과를 반환한다.
        ReservationStatus facilityStatus = resolveFacilityStatus(req.getStatus());
        if (req.getStatus() != null && !req.getStatus().isBlank() && facilityStatus == null) {
            return List.of();
        }

        List<Reservation> reservations = facilityStatus != null
                ? reservationRepository.findAdminReservationsForOverviewByStatus(
                        complexId, facilityStatus, req.getFacilityId(), req.getReservationDate())
                : reservationRepository.findAdminReservationsForOverview(
                        complexId, req.getFacilityId(), req.getReservationDate());

        if (reservations.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = reservations.stream()
                .map(Reservation::getUserId).filter(id -> id != null).distinct().toList();
        List<Long> householdIds = reservations.stream()
                .map(Reservation::getHouseholdId).filter(id -> id != null).distinct().toList();
        List<Long> facilityIds = reservations.stream()
                .map(Reservation::getFacilityId).filter(id -> id != null).distinct().toList();

        Map<Long, UserCache> userMap = userIds.isEmpty() ? Map.of()
                : userCacheRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(UserCache::getId, Function.identity()));
        Map<Long, HouseholdCache> householdMap = householdIds.isEmpty() ? Map.of()
                : householdCacheRepository.findAllById(householdIds).stream()
                        .collect(Collectors.toMap(HouseholdCache::getHouseholdId, Function.identity()));
        Map<Long, Facility> facilityMap = facilityIds.isEmpty() ? Map.of()
                : facilityRepository.findAllById(facilityIds).stream()
                        .collect(Collectors.toMap(Facility::getId, Function.identity()));

        return reservations.stream().map(r -> {
            UserCache user = userMap.get(r.getUserId());
            HouseholdCache household = householdMap.get(r.getHouseholdId());
            Facility facility = facilityMap.get(r.getFacilityId());
            return AdminReservationOverviewRes.builder()
                    .reservationId(r.getId())
                    .reservationKind(ReservationKind.FACILITY)
                    .facilityId(r.getFacilityId())
                    .facilityName(facility != null ? facility.getName() : null)
                    .userId(r.getUserId())
                    .householdId(r.getHouseholdId())
                    .residentName(user != null ? user.getName() : null)
                    .dong(household != null ? household.getBuildingNo() : null)
                    .ho(household != null ? household.getUnitNo() : null)
                    .unit(buildUnit(household))
                    .status(r.getStatus().name())
                    .statusName(r.getStatus().getValue())
                    .cancelable(r.getStatus() == ReservationStatus.CONFIRMED)
                    .reservationDate(r.getReservationDate())
                    .startTime(r.getStartTime())
                    .endTime(r.getEndTime())
                    .createdAt(r.getCreatedAt())
                    .build();
        }).toList();
    }

    private List<AdminReservationOverviewRes> fetchGxItems(Long complexId, AdminReservationOverviewReq req) {
        // status 파라미터를 GxReservationStatus로 변환한다. COMPLETED는 GX에 해당 없으므로 빈 결과를 반환한다.
        GxReservationStatus gxStatus = resolveGxStatus(req.getStatus());
        if (req.getStatus() != null && !req.getStatus().isBlank() && gxStatus == null) {
            return List.of();
        }

        List<GxReservation> gxList = gxStatus != null
                ? gxReservationRepository.findAdminGxReservationsForOverviewByStatus(
                        complexId, gxStatus, req.getFacilityId())
                : gxReservationRepository.findAdminGxReservationsForOverview(
                        complexId, req.getFacilityId());

        if (gxList.isEmpty()) {
            return List.of();
        }

        List<Long> programIds = gxList.stream()
                .map(GxReservation::getProgramId).filter(id -> id != null).distinct().toList();
        List<Long> userIds = gxList.stream()
                .map(GxReservation::getUserId).filter(id -> id != null).distinct().toList();
        List<Long> householdIds = gxList.stream()
                .map(GxReservation::getHouseholdId).filter(id -> id != null).distinct().toList();

        Map<Long, GxProgram> programMap = programIds.isEmpty() ? Map.of()
                : gxProgramRepository.findByIdIn(programIds).stream()
                        .collect(Collectors.toMap(GxProgram::getId, Function.identity()));
        List<Long> facilityIds = programMap.values().stream()
                .map(GxProgram::getFacilityId).filter(id -> id != null).distinct().toList();

        Map<Long, UserCache> userMap = userIds.isEmpty() ? Map.of()
                : userCacheRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(UserCache::getId, Function.identity()));
        Map<Long, HouseholdCache> householdMap = householdIds.isEmpty() ? Map.of()
                : householdCacheRepository.findAllById(householdIds).stream()
                        .collect(Collectors.toMap(HouseholdCache::getHouseholdId, Function.identity()));
        Map<Long, Facility> facilityMap = facilityIds.isEmpty() ? Map.of()
                : facilityRepository.findAllById(facilityIds).stream()
                        .collect(Collectors.toMap(Facility::getId, Function.identity()));

        return gxList.stream().map(r -> {
            UserCache user = userMap.get(r.getUserId());
            HouseholdCache household = householdMap.get(r.getHouseholdId());
            GxProgram program = programMap.get(r.getProgramId());
            Facility facility = program != null ? facilityMap.get(program.getFacilityId()) : null;
            boolean cancelable = r.getStatus() == GxReservationStatus.WAITING
                    || r.getStatus() == GxReservationStatus.CONFIRMED;
            return AdminReservationOverviewRes.builder()
                    .reservationId(r.getId())
                    .gxReservationId(r.getId())
                    .reservationKind(ReservationKind.GX)
                    .facilityId(program != null ? program.getFacilityId() : null)
                    .facilityName(facility != null ? facility.getName() : null)
                    .programId(r.getProgramId())
                    .programName(program != null ? program.getName() : null)
                    .userId(r.getUserId())
                    .householdId(r.getHouseholdId())
                    .residentName(user != null ? user.getName() : null)
                    .dong(household != null ? household.getBuildingNo() : null)
                    .ho(household != null ? household.getUnitNo() : null)
                    .unit(buildUnit(household))
                    .status(r.getStatus().name())
                    .statusName(r.getStatus().getValue())
                    .cancelable(cancelable)
                    .startDate(program != null ? program.getStartDate() : null)
                    .endDate(program != null ? program.getEndDate() : null)
                    .startTime(program != null ? program.getStartTime() : null)
                    .endTime(program != null ? program.getEndTime() : null)
                    .createdAt(r.getCreatedAt())
                    .build();
        }).toList();
    }

    // status 문자열을 ReservationStatus로 변환한다. WAITING은 FACILITY에 없으므로 null을 반환한다.
    private ReservationStatus resolveFacilityStatus(String status) {
        if (status == null || status.isBlank()) return null;
        return switch (status.toUpperCase()) {
            case "CONFIRMED" -> ReservationStatus.CONFIRMED;
            case "CANCELLED" -> ReservationStatus.CANCELLED;
            case "COMPLETED" -> ReservationStatus.COMPLETED;
            default -> null;
        };
    }

    // status 문자열을 GxReservationStatus로 변환한다. COMPLETED는 GX에 없으므로 null을 반환한다.
    private GxReservationStatus resolveGxStatus(String status) {
        if (status == null || status.isBlank()) return null;
        return switch (status.toUpperCase()) {
            case "CONFIRMED" -> GxReservationStatus.CONFIRMED;
            case "CANCELLED" -> GxReservationStatus.CANCELLED;
            case "WAITING" -> GxReservationStatus.WAITING;
            case "REJECTED" -> GxReservationStatus.REJECTED;
            default -> null;
        };
    }

    private String buildUnit(HouseholdCache household) {
        if (household == null
                || household.getBuildingNo() == null
                || household.getUnitNo() == null) {
            return null;
        }
        return household.getBuildingNo() + "동 " + household.getUnitNo() + "호";
    }

    private PageResponse<AdminReservationOverviewRes> buildPageResponse(
            List<AdminReservationOverviewRes> items, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 20;
        long total = items.size();
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / safeSize);
        int fromIndex = safePage * safeSize;
        int toIndex = (int) Math.min((long) fromIndex + safeSize, total);

        List<AdminReservationOverviewRes> content = fromIndex >= total
                ? List.of()
                : items.subList(fromIndex, toIndex);

        return PageResponse.<AdminReservationOverviewRes>builder()
                .content(content)
                .page(safePage)
                .size(safeSize)
                .totalElements(total)
                .totalPages(totalPages)
                .hasNext(toIndex < total)
                .build();
    }
}
