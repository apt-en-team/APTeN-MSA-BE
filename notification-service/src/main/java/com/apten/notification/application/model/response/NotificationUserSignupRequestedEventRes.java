package com.apten.notification.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원가입 신청 알림 이벤트 처리 결과 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUserSignupRequestedEventRes {
    private Boolean consumed;
    private Integer createdCount;
}
