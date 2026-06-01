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

// 시설예약 알림 내부 API 연동
@Slf4j
@Service
@RequiredArgsConstructor
public class FacilityNotificationService {

    // 알림 유형 코드
    private static final String TYPE_FACILITY_RESERVED = "FACILITY_RESERVED";
    private static final String TYPE_FACILITY_CANCELLED = "FACILITY_CANCELLED";
    private static final String TYPE_GX_APPLIED = "GX_APPLIED";
    private static final String TYPE_GX_APPROVED = "GX_APPROVED";
    private static final String TYPE_GX_REJECTED = "GX_REJECTED";
    private static final String TYPE_GX_MINIMUM_REACHED = "GX_MINIMUM_REACHED";
    private static final String TYPE_GX_APPROVAL_REMINDER = "GX_APPROVAL_REMINDER";

    // 알림 대상 유형 코드
    private static final String TARGET_FACILITY_RESERVATION = "FACILITY_RESERVATION";
    private static final String TARGET_GX_PROGRAM = "GX_PROGRAM";

    private final NotificationInternalClient notificationInternalClient;
    private final ObjectMapper objectMapper;

    // 시설 예약 완료 알림
    public void notifyFacilityReserved(Long userId, Long complexId, Reservation reservation, Facility facility) {
        // 예약 ID 기준 알림 대상 설정
        NotificationCreateReq request = NotificationCreateReq.builder()
                .receiverUserId(userId)
                .complexId(complexId)
                .type(TYPE_FACILITY_RESERVED)
                .targetType(TARGET_FACILITY_RESERVATION)
                .targetId(reservation.getId())
                .title("시설 예약이 완료되었습니다.")
                .content(facility.getName() + " 예약이 완료되었습니다.")
                .linkPath(buildReservationLink(complexId, reservation.getId()))
                // 시설 예약 부가 정보
                .payloadJson(toPayloadJson(Map.of(
                        "facilityId", facility.getId(),
                        "facilityName", facility.getName(),
                        "reservationId", reservation.getId()
                )))
                .build();

        sendAfterCommit("시설 예약 완료 알림", request);
    }

    // 시설 예약 취소 알림
    public void notifyFacilityCancelled(Long userId, Long complexId, Reservation reservation, Facility facility) {
        // 시설명 조회 실패 기본값
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
                // 취소 예약 부가 정보
                .payloadJson(toPayloadJson(Map.of(
                        "reservationId", reservation.getId()
                )))
                .build();

        sendAfterCommit("시설 예약 취소 알림", request);
    }

    // GX 신청 접수 알림
    public void notifyGxApplied(Long userId, Long complexId, GxReservation reservation, GxProgram program) {
        // GX 프로그램 ID 기준 알림 대상 설정
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

    // GX 승인 알림
    public void notifyGxApproved(Long userId, Long complexId, GxReservation reservation, GxProgram program) {
        // 신청자 기준 결과 알림
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

    // GX 거절 알림
    public void notifyGxRejected(Long userId, Long complexId, GxReservation reservation, GxProgram program) {
        // 거절/취소 결과 통합 안내
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

    // GX 최소 인원 충족 알림 (관리자 broadcast)
    public void notifyGxMinimumReached(Long complexId, GxProgram program) {
        NotificationAdminBroadcastReq request = NotificationAdminBroadcastReq.builder()
                .complexId(complexId)
                .type(TYPE_GX_MINIMUM_REACHED)
                .targetType(TARGET_GX_PROGRAM)
                .targetId(program.getId())
                .title("GX 최소 인원이 충족되었습니다.")
                .content(program.getName() + " 신청 인원이 최소 인원에 도달했습니다. 승인 처리를 확인해주세요.")
                // 관리자 GX 목록 이동
                .linkPath(buildAdminGxLink(complexId, program.getId()))
                .build();

        sendAdminBroadcastAfterCommit("GX 최소 인원 충족 관리자 알림", request);
    }

    // GX 승인 리마인더 알림 (관리자 broadcast)
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

    // 알림 생성 예약 (afterCommit)
    private void sendAfterCommit(String operationName, NotificationCreateReq request) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 트랜잭션 외부 즉시 발송
            sendBestEffort(operationName, request);
            return;
        }

        // 원본 트랜잭션 커밋 후 발송
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendBestEffort(operationName, request);
            }
        });
    }

    // 알림 생성 요청 (best-effort)
    private void sendBestEffort(String operationName, NotificationCreateReq request) {
        // 내부 API 즉시 재시도 (최대 3회)
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
                    // 원본 기능 성공 유지
                    log.warn("{} 생성 실패 ({}회 시도 후). receiverUserId={}, targetType={}, targetId={}",
                            operationName, maxAttempts,
                            request.getReceiverUserId(), request.getTargetType(), request.getTargetId(),
                            exception);
                }
            }
        }
    }

    // 관리자 broadcast 예약 (afterCommit)
    private void sendAdminBroadcastAfterCommit(String operationName, NotificationAdminBroadcastReq request) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            sendAdminBroadcastBestEffort(operationName, request);
            return;
        }
        // 원본 트랜잭션 커밋 후 발송
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendAdminBroadcastBestEffort(operationName, request);
            }
        });
    }

    // 관리자 broadcast 요청 (best-effort)
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
                    // 원본 기능 성공 유지
                    log.warn("{} 생성 실패 ({}회 시도 후). complexId={}, type={}",
                            operationName, maxAttempts, request.getComplexId(), request.getType(), exception);
                }
            }
        }
    }

    // 관리자 GX 목록 이동 경로 생성
    private String buildAdminGxLink(Long complexId, Long programId) {
        // 관리자 GX 목록 기준 이동
        return "/admin/gx-programs";
    }

    // 시설 예약 상세 이동 경로 생성
    private String buildReservationLink(Long complexId, Long reservationId) {
        // 입주민 예약 상세 경로
        return "/resident/" + complexId + "/reservations/" + reservationId;
    }

    // 입주민 GX 상세 이동 경로 생성
    private String buildGxReservationLink(Long complexId, Long programId, Long gxReservationId, String status) {
        // 입주민 GX 상세 경로
        return "/resident/" + complexId + "/facility/gx-programs/" + programId
                + "?from=reservations&gxReservationId=" + gxReservationId + "&status=" + status;
    }

    // 알림 부가 정보 JSON 변환
    private String toPayloadJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            // 부가 정보 실패 시 알림 생성 유지
            log.warn("알림 payloadJson 직렬화 실패. payload={}", payload, exception);
            return null;
        }
    }
}
