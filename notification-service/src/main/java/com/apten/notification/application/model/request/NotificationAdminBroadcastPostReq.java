package com.apten.notification.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자/매니저 복수 대상 알림 생성 요청 DTO
// complexId 기준으로 ACTIVE 상태의 ADMIN, MANAGER 전체에게 각각 알림을 생성한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationAdminBroadcastPostReq {
    // 알림을 보낼 단지 ID (ACTIVE ADMIN/MANAGER 조회 기준)
    private Long complexId;
    // NotificationType name (예: GX_MINIMUM_REACHED, SIGNUP_REQUESTED)
    private String type;
    // NotificationTargetType name (예: GX_PROGRAM, USER)
    private String targetType;
    // 알림과 연결된 도메인 대상 ID
    private Long targetId;
    // 알림 제목
    private String title;
    // 알림 본문
    private String content;
    // 클릭 시 이동할 프론트 경로
    private String linkPath;
    // 도메인별 추가 정보를 문자열 JSON으로 보관한다
    private String payloadJson;
}
