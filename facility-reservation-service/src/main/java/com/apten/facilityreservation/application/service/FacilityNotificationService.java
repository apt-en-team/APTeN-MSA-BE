package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.infrastructure.client.NotificationInternalClient;
import com.apten.facilityreservation.infrastructure.client.model.NotificationAdminBroadcastReq;
import com.apten.facilityreservation.infrastructure.client.model.NotificationCreateReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

// 시설예약 도메인에서 발생한 성공 이벤트를 notification-service 내부 API 요청으로 바꾸는 서비스이다
// Kafka 전환 전 1차 연동이므로 이 클래스 한 곳에 내부 API 호출 책임을 모아 둔다
@Slf4j
@Service
@RequiredArgsConstructor
public class FacilityNotificationService {

    // notification-service의 NotificationType 문자열과 맞춰야 한다
    private static final String TYPE_FACILITY_RESERVED = "FACILITY_RESERVED";
    private static final String TYPE_FACILITY_CANCELLED = "FACILITY_CANCELLED";
    private static final String TYPE_GX_APPLIED = "GX_APPLIED";
    private static final String TYPE_GX_APPROVED = "GX_APPROVED";
    private static final String TYPE_GX_REJECTED = "GX_REJECTED";
    private static final String TYPE_GX_MINIMUM_REACHED = "GX_MINIMUM_REACHED";
    private static final String TYPE_GX_APPROVAL_REMINDER = "GX_APPROVAL_REMINDER";

    // notification-service의 NotificationTargetType 문자열과 맞춰야 한다
    private static final String TARGET_FACILITY_RESERVATION = "FACILITY_RESERVATION";
    private static final String TARGET_GX_PROGRAM = "GX_PROGRAM";

    private final NotificationInternalClient notificationInternalClient;
    private final ObjectMapper objectMapper;

    public void notifyFacilityReserved(Long userId, Long complexId, Reservation reservation, Facility facility) {
        // 예약 생성 알림은 예약 row id를 targetId로 사용해 클릭/읽음 처리 기준을 명확히 한다
        // content는 사용자가 바로 이해할 수 있게 시설명을 포함한다
        NotificationCreateReq request = NotificationCreateReq.builder()
                .receiverUserId(userId)
                .complexId(complexId)
                .type(TYPE_FACILITY_RESERVED)
                .targetType(TARGET_FACILITY_RESERVATION)
                .targetId(reservation.getId())
                .title("시설 예약이 완료되었습니다.")
                .content(facility.getName() + " 예약이 완료되었습니다.")
                .linkPath(buildReservationLink(complexId, reservation.getId()))
                // payloadJson은 화면 이동에는 필수는 아니지만 추후 상세 표시나 디버깅에 쓸 수 있다
                .payloadJson(toPayloadJson(Map.of(
                        "facilityId", facility.getId(),
                        "facilityName", facility.getName(),
                        "reservationId", reservation.getId()
                )))
                .build();

        sendAfterCommit("시설 예약 완료 알림", request);
    }

    public void notifyFacilityCancelled(Long userId, Long complexId, Reservation reservation, Facility facility) {
        // 취소 시점에는 시설 조회가 실패해도 알림 자체는 보낼 수 있게 기본 문구를 준비한다
        String facilityName = facility == null ? "시설" : facility.getName();
        NotificationCreateReq request = NotificationCreateReq.builder()
                .receiverUserId(userId)
                .complexId(complexId)
                .type(TYPE_FACILITY_CANCELLED)
                .targetType(TARGET_FACILITY_RESERVATION)
                .targetId(reservation.getId())
                .title("시설 예약이 취소되었습니다.")
                .content(facilityName + " 예약이 취소되었습니다.")
                .linkPath(buildReservationLink(complexId, reservation.getId()))
                // 취소 알림은 예약 ID만 있어도 내 예약 상세로 이동할 수 있다
                .payloadJson(toPayloadJson(Map.of(
                        "reservationId", reservation.getId()
                )))
                .build();

        sendAfterCommit("시설 예약 취소 알림", request);
    }

    public void notifyGxApplied(Long userId, Long complexId, GxReservation reservation, GxProgram program) {
        // GX 신청 알림은 프로그램 상세 화면으로 이동하므로 programId를 targetId로 사용한다
        // gxReservationId는 내 예약 진입 상태를 query와 payloadJson에 함께 남긴다
        NotificationCreateReq request = NotificationCreateReq.builder()
                .receiverUserId(userId)
                .complexId(complexId)
                .type(TYPE_GX_APPLIED)
                .targetType(TARGET_GX_PROGRAM)
                .targetId(program.getId())
                .title("GX 신청이 접수되었습니다.")
                .content(program.getName() + " 신청이 접수되었습니다.")
                .linkPath(buildGxReservationLink(complexId, program.getId(), reservation.getId(), reservation.getStatus().name()))
                .payloadJson(toPayloadJson(Map.of(
                        "programId", program.getId(),
                        "gxReservationId", reservation.getId(),
                        "programName", program.getName()
                )))
                .build();

        sendAfterCommit("GX 신청 접수 알림", request);
    }

    public void notifyGxApproved(Long userId, Long complexId, GxReservation reservation, GxProgram program) {
        // 관리자가 승인한 뒤 입주민에게 결과를 알려야 하므로 신청자 userId를 수신자로 사용한다
        NotificationCreateReq request = NotificationCreateReq.builder()
                .receiverUserId(userId)
                .complexId(complexId)
                .type(TYPE_GX_APPROVED)
                .targetType(TARGET_GX_PROGRAM)
                .targetId(program.getId())
                .title("GX 신청이 승인되었습니다.")
                .content(program.getName() + " 신청이 승인되었습니다.")
                .linkPath(buildGxReservationLink(complexId, program.getId(), reservation.getId(), reservation.getStatus().name()))
                .payloadJson(toPayloadJson(Map.of(
                        "programId", program.getId(),
                        "gxReservationId", reservation.getId(),
                        "programName", program.getName()
                )))
                .build();

        sendAfterCommit("GX 신청 승인 알림", request);
    }

    public void notifyGxRejected(Long userId, Long complexId, GxReservation reservation, GxProgram program) {
        // 관리자 거절/취소는 입주민 관점에서 신청 반려 알림으로 동일하게 전달한다
        NotificationCreateReq request = NotificationCreateReq.builder()
                .receiverUserId(userId)
                .complexId(complexId)
                .type(TYPE_GX_REJECTED)
                .targetType(TARGET_GX_PROGRAM)
                .targetId(program.getId())
                .title("GX 신청이 거절되었습니다.")
                .content(program.getName() + " 신청이 거절되었습니다.")
                .linkPath(buildGxReservationLink(complexId, program.getId(), reservation.getId(), reservation.getStatus().name()))
                .payloadJson(toPayloadJson(Map.of(
                        "programId", program.getId(),
                        "gxReservationId", reservation.getId(),
                        "programName", program.getName()
                )))
                .build();

        sendAfterCommit("GX 신청 거절 알림", request);
    }

    public void notifyGxMinimumReached(Long complexId, GxProgram program) {
        NotificationAdminBroadcastReq request = NotificationAdminBroadcastReq.builder()
                .complexId(complexId)
                .type(TYPE_GX_MINIMUM_REACHED)
                .targetType(TARGET_GX_PROGRAM)
                .targetId(program.getId())
                .title("GX 최소 인원이 충족되었습니다.")
                .content(program.getName() + " 신청 인원이 최소 인원에 도달했습니다. 승인 처리를 확인해주세요.")
                // 관리자 GX 프로그램 화면 경로: 프론트 adminRoutes 기준으로 확인 필요
                .linkPath(buildAdminGxLink(complexId, program.getId()))
                .build();

        sendAdminBroadcastAfterCommit("GX 최소 인원 충족 관리자 알림", request);
    }

    public void notifyGxApprovalReminder(Long complexId, GxProgram program) {
        NotificationAdminBroadcastReq request = NotificationAdminBroadcastReq.builder()
                .complexId(complexId)
                .type(TYPE_GX_APPROVAL_REMINDER)
                .targetType(TARGET_GX_PROGRAM)
                .targetId(program.getId())
                .title("GX 승인 대기 신청이 있습니다.")
                .content(program.getName() + " 시작일이 가까워졌습니다. 승인 대기 신청을 확인해주세요.")
                .linkPath(buildAdminGxLink(complexId, program.getId()))
                .build();

        sendAdminBroadcastBestEffort("GX 승인 리마인더 관리자 알림", request);
    }

    private void sendAfterCommit(String operationName, NotificationCreateReq request) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 트랜잭션 밖에서 호출되는 테스트/배치 상황은 바로 best-effort로 보낸다
            sendBestEffort(operationName, request);
            return;
        }

        // 예약 트랜잭션이 성공한 뒤 알림을 생성해야 롤백된 예약의 알림이 남지 않는다
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendBestEffort(operationName, request);
            }
        });
    }

    private void sendBestEffort(String operationName, NotificationCreateReq request) {
        // 일시적인 네트워크 오류를 흡수하기 위해 최대 3회 즉시 재시도한다
        // afterCommit 스레드를 오래 점유하지 않도록 sleep 없이 빠르게 재시도한다
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                notificationInternalClient.createNotification(request);
                return;
            } catch (Exception exception) {
                if (attempt < maxAttempts) {
                    log.debug("{} 알림 발송 재시도. attempt={}/{}, targetId={}",
                            operationName, attempt + 1, maxAttempts, request.getTargetId());
                } else {
                    // 알림 장애가 예약/취소/GX 신청 성공을 막으면 안 되므로 예외를 다시 던지지 않는다
                    log.warn("{} 생성 실패 ({}회 시도 후). receiverUserId={}, targetType={}, targetId={}",
                            operationName, maxAttempts,
                            request.getReceiverUserId(), request.getTargetType(), request.getTargetId(),
                            exception);
                }
            }
        }
    }

    private void sendAdminBroadcastAfterCommit(String operationName, NotificationAdminBroadcastReq request) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            sendAdminBroadcastBestEffort(operationName, request);
            return;
        }
        // 도메인 트랜잭션 커밋 이후 관리자 broadcast를 보내야 롤백된 이벤트의 알림이 남지 않는다
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendAdminBroadcastBestEffort(operationName, request);
            }
        });
    }

    private void sendAdminBroadcastBestEffort(String operationName, NotificationAdminBroadcastReq request) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                notificationInternalClient.createAdminBroadcastNotification(request);
                return;
            } catch (Exception exception) {
                if (attempt < maxAttempts) {
                    log.debug("{} 관리자 broadcast 재시도. attempt={}/{}, complexId={}",
                            operationName, attempt + 1, maxAttempts, request.getComplexId());
                } else {
                    // 알림 장애가 원 기능 성공을 막으면 안 되므로 예외를 다시 던지지 않는다
                    log.warn("{} 생성 실패 ({}회 시도 후). complexId={}, type={}",
                            operationName, maxAttempts, request.getComplexId(), request.getType(), exception);
                }
            }
        }
    }

    private String buildAdminGxLink(Long complexId, Long programId) {
        // 관리자 화면은 complexId를 URL에 포함하지 않으며, GX 목록에서 모달로 상세/승인 처리한다
        return "/admin/gx-programs";
    }

    private String buildReservationLink(Long complexId, Long reservationId) {
        // FE residentRoutes 기준: /resident/:complexId/reservations/:reservationId
        return "/resident/" + complexId + "/reservations/" + reservationId;
    }

    private String buildGxReservationLink(Long complexId, Long programId, Long gxReservationId, String status) {
        // FE 라우터 기준 GX 상세 화면으로 이동하되, 내 예약 진입 상태를 query로 전달한다
        return "/resident/" + complexId + "/facility/gx-programs/" + programId
                + "?from=reservations&gxReservationId=" + gxReservationId + "&status=" + status;
    }

    private String toPayloadJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            // payloadJson은 부가 정보라 직렬화 실패해도 알림 생성 자체는 계속 진행한다
            log.warn("알림 payloadJson 직렬화 실패. payload={}", payload, exception);
            return null;
        }
    }
}
