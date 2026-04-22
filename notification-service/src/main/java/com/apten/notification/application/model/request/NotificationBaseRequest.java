package com.apten.notification.application.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

// notification-service 요청 모델의 공통 시작점으로 두는 최소 요청 DTO
// 알림 발송과 조회 요청 모델을 같은 패키지 규칙으로 확장하기 위한 기준 파일이다
@Getter
@NoArgsConstructor
public class NotificationBaseRequest {
}
