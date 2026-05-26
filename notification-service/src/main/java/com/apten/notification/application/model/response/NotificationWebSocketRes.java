package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationWebSocketRes {

    // 프론트가 알림 종류별 아이콘과 문구를 분기할 때 쓰는 enum 이름
    private String type;
    // DB에는 code converter 기준 코드값이 저장되므로 함께 내려준다
    private String typeCode;
    // 화면 표시용 한글 유형명
    private String typeValue;
    // 클릭/읽음 처리에 사용할 DB 알림 ID
    private Long notificationId;
    // 드롭다운과 목록에 바로 보여줄 제목
    private String title;
    // 드롭다운과 목록에 바로 보여줄 본문
    private String content;
    // 알림 클릭 시 이동할 프론트 경로
    private String linkPath;
    // 신규 알림 수신 직후 배지 동기화에 사용할 미읽음 수
    private Long unreadCount;
    // 알림 생성 시각
    private LocalDateTime createdAt;
}
