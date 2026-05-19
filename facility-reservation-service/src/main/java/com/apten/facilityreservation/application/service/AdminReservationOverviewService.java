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
import com.apten.facilityreservation.domain.enums.ReservationKind;
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

        return buildPageResponse(items, req.getPage(), req.getSize());
    }

    private List<AdminReservationOverviewRes> fetchFacilityItems(Long complexId, AdminReservationOverviewReq req) {
        List<Reservation> reservations = req.getFacilityStatus() != null
                ? reservationRepository.findAdminReservationsForOverviewByStatus(
                        complexId, req.getFacilityStatus(), req.getFacilityId(), req.getReservationDate())
                : reservationRepository.findAdminReservationsForOverview(
                        complexId, req.getFacilityId(), req.getReservationDate());

        if (reservations.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = reservations.stream().map(Reservation::getUserId).distinct().toList();
        List<Long> householdIds = reservations.stream().map(Reservation::getHouseholdId).distinct().toList();
        List<Long> facilityIds = reservations.stream().map(Reservation::getFacilityId).distinct().toList();

        Map<Long, UserCache> userMap = userCacheRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserCache::getId, Function.identity()));
        Map<Long, HouseholdCache> householdMap = householdCacheRepository.findAllById(householdIds).stream()
                .collect(Collectors.toMap(HouseholdCache::getHouseholdId, Function.identity()));
        Map<Long, Facility> facilityMap = facilityRepository.findAllById(facilityIds).stream()
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
                    .status(r.getStatus().name())
                    .statusName(r.getStatus().getValue())
                    .reservationDate(r.getReservationDate())
                    .createdAt(r.getCreatedAt())
                    .build();
        }).toList();
    }

    private List<AdminReservationOverviewRes> fetchGxItems(Long complexId, AdminReservationOverviewReq req) {
        List<GxReservation> gxList = req.getGxStatus() != null
                ? gxReservationRepository.findAdminGxReservationsForOverviewByStatus(
                        complexId, req.getGxStatus(), req.getFacilityId())
                : gxReservationRepository.findAdminGxReservationsForOverview(
                        complexId, req.getFacilityId());

        if (gxList.isEmpty()) {
            return List.of();
        }

        List<Long> programIds = gxList.stream().map(GxReservation::getProgramId).distinct().toList();
        List<Long> userIds = gxList.stream().map(GxReservation::getUserId).distinct().toList();
        List<Long> householdIds = gxList.stream().map(GxReservation::getHouseholdId).distinct().toList();

        Map<Long, GxProgram> programMap = gxProgramRepository.findByIdIn(programIds).stream()
                .collect(Collectors.toMap(GxProgram::getId, Function.identity()));
        List<Long> facilityIds = programMap.values().stream()
                .map(GxProgram::getFacilityId).distinct().toList();

        Map<Long, UserCache> userMap = userCacheRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserCache::getId, Function.identity()));
        Map<Long, HouseholdCache> householdMap = householdCacheRepository.findAllById(householdIds).stream()
                .collect(Collectors.toMap(HouseholdCache::getHouseholdId, Function.identity()));
        Map<Long, Facility> facilityMap = facilityRepository.findAllById(facilityIds).stream()
                .collect(Collectors.toMap(Facility::getId, Function.identity()));

        return gxList.stream().map(r -> {
            UserCache user = userMap.get(r.getUserId());
            HouseholdCache household = householdMap.get(r.getHouseholdId());
            GxProgram program = programMap.get(r.getProgramId());
            Facility facility = program != null ? facilityMap.get(program.getFacilityId()) : null;
            return AdminReservationOverviewRes.builder()
                    .reservationId(r.getId())
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
                    .status(r.getStatus().name())
                    .statusName(r.getStatus().getValue())
                    .createdAt(r.getCreatedAt())
                    .build();
        }).toList();
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
