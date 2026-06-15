package com.apten.facilityreservation.application.service;

import com.apten.common.kafka.payload.NotificationAdminBroadcastEventPayload;
import com.apten.common.kafka.payload.NotificationEventPayload;
import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.infrastructure.kafka.FacilityReservationOutboxService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// 시설예약 알림 Outbox 적재 진입점
@Slf4j
@Service
@RequiredArgsConstructor
public class FacilityNotificationService {

    private static final String TYPE_GX_APPROVED = "GX_APPROVED";
    private static final String TYPE_GX_REJECTED = "GX_REJECTED";
    private static final String TYPE_GX_MINIMUM_REACHED = "GX_MINIMUM_REACHED";
    private static final String TYPE_GX_APPROVAL_REMINDER = "GX_APPROVAL_REMINDER";
    private static final String TYPE_RESERVATION_FORCE_CANCELLED = "RESERVATION_FORCE_CANCELLED";

    private static final String TARGET_FACILITY_RESERVATION = "FACILITY_RESERVATION";
    private static final String TARGET_GX_PROGRAM = "GX_PROGRAM";

    // Outbox 비활성 환경(로컬 등)에서는 Optional로 주입된다
    private final Optional<FacilityReservationOutboxService> outboxService;

    // GX 승인 알림
    public void notifyGxApproved(Long userId, Long complexId, GxReservation reservation, GxProgram program) {
        outboxService.ifPresent(service -> service.saveNotificationEvent(
                NotificationEventPayload.builder()
                        .receiverUserId(userId)
                        .complexId(complexId)
                        .type(TYPE_GX_APPROVED)
                        .targetType(TARGET_GX_PROGRAM)
                        .targetId(program.getId())
                        .title("GX 신청이 승인되었습니다.")
                        .content(program.getName() + " 신청이 승인되었습니다.")
                        .linkPath(buildGxLink(complexId, program.getId(), reservation.getId(), reservation.getStatus().name()))
                        .build()
        ));
    }

    // GX 거절 알림
    public void notifyGxRejected(Long userId, Long complexId, GxReservation reservation, GxProgram program) {
        outboxService.ifPresent(service -> service.saveNotificationEvent(
                NotificationEventPayload.builder()
                        .receiverUserId(userId)
                        .complexId(complexId)
                        .type(TYPE_GX_REJECTED)
                        .targetType(TARGET_GX_PROGRAM)
                        .targetId(program.getId())
                        .title("GX 신청이 거절되었습니다.")
                        .content(program.getName() + " 신청이 거절되었습니다.")
                        .linkPath(buildGxLink(complexId, program.getId(), reservation.getId(), reservation.getStatus().name()))
                        .build()
        ));
    }

    // GX 최소 인원 충족 알림 (관리자 broadcast)
    public void notifyGxMinimumReached(Long complexId, GxProgram program) {
        outboxService.ifPresent(service -> service.saveAdminBroadcastNotificationEvent(
                NotificationAdminBroadcastEventPayload.builder()
                        .complexId(complexId)
                        .type(TYPE_GX_MINIMUM_REACHED)
                        .targetType(TARGET_GX_PROGRAM)
                        .targetId(program.getId())
                        .title("GX 최소 인원이 충족되었습니다.")
                        .content(program.getName() + " 신청 인원이 최소 인원에 도달했습니다. 승인 처리를 확인해주세요.")
                        .linkPath("/admin/gx-programs")
                        .build()
        ));
    }

    // GX 승인 리마인더 알림 (관리자 broadcast)
    public void notifyGxApprovalReminder(Long complexId, GxProgram program) {
        outboxService.ifPresent(service -> service.saveAdminBroadcastNotificationEvent(
                NotificationAdminBroadcastEventPayload.builder()
                        .complexId(complexId)
                        .type(TYPE_GX_APPROVAL_REMINDER)
                        .targetType(TARGET_GX_PROGRAM)
                        .targetId(program.getId())
                        .title("GX 승인 대기 신청이 있습니다.")
                        .content(program.getName() + " 시작일이 가까워졌습니다. 승인 대기 신청을 확인해주세요.")
                        .linkPath("/admin/gx-programs")
                        .build()
        ));
    }

    // 관리자 강제 취소 알림
    public void notifyReservationForceCancelled(Long userId, Long complexId, Long reservationId, String facilityName) {
        outboxService.ifPresent(service -> service.saveNotificationEvent(
                NotificationEventPayload.builder()
                        .receiverUserId(userId)
                        .complexId(complexId)
                        .type(TYPE_RESERVATION_FORCE_CANCELLED)
                        .targetType(TARGET_FACILITY_RESERVATION)
                        .targetId(reservationId)
                        .title("예약이 취소되었습니다.")
                        .content(facilityName + " 예약이 관리자에 의해 취소되었습니다.")
                        .linkPath("/resident/" + complexId + "/reservations/" + reservationId)
                        .build()
        ));
    }

    // 시설 이용 1시간 전 리마인더 알림
    public void notifyFacilityReminder(Long userId, Long complexId, Long reservationId, String facilityName) {
        outboxService.ifPresent(service -> service.saveNotificationEvent(
                NotificationEventPayload.builder()
                        .receiverUserId(userId)
                        .complexId(complexId)
                        .type("FACILITY_REMINDER")
                        .targetType(TARGET_FACILITY_RESERVATION)
                        .targetId(reservationId)
                        .title("시설 이용 1시간 전입니다.")
                        .content(facilityName + " 이용 시작 1시간 전입니다.")
                        .linkPath("/resident/" + complexId + "/reservations/" + reservationId)
                        .build()
        ));
    }

    private String buildGxLink(Long complexId, Long programId, Long gxReservationId, String status) {
        return "/resident/" + complexId + "/facility/gx-programs/" + programId
                + "?from=reservations&gxReservationId=" + gxReservationId + "&status=" + status;
    }
}
