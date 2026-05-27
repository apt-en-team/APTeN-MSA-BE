package com.apten.notification.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType implements EnumMapperType {

    // 1차 WebSocket 대상과 HTTPS 이후 FCM 대상 알림을 같은 enum에서 관리한다
    FACILITY_RESERVED("01", "시설 예약 완료"),
    FACILITY_CANCELLED("02", "시설 예약 취소"),
    GX_APPLIED("03", "GX 신청 접수"),
    GX_MINIMUM_REACHED("04", "GX 최소 인원 충족"),
    GX_APPROVAL_REMINDER("05", "GX 승인 요청 리마인더"),
    SIGNUP_REQUESTED("06", "회원가입 승인 요청"),
    VEHICLE_REGISTRATION_REQUESTED("07", "차량 등록 승인 요청"),
    GX_APPROVED("08", "GX 승인"),
    GX_REJECTED("09", "GX 거절"),
    FACILITY_REMINDER("10", "시설 이용 전 리마인더"),
    VEHICLE_REGISTRATION_APPROVED("11", "차량 등록 승인"),
    VEHICLE_REGISTRATION_REJECTED("12", "차량 등록 반려"),
    VEHICLE_ENTRY("13", "등록 차량 입차"),
    VEHICLE_EXIT("14", "등록 차량 출차"),
    VISITOR_VEHICLE_ENTRY("15", "방문 차량 입차"),
    VISITOR_VEHICLE_EXIT("16", "방문 차량 출차"),
    SIGNUP_APPROVED("17", "회원가입 승인"),
    SIGNUP_REJECTED("18", "회원가입 반려"),
    NOTICE_CREATED("19", "공지사항 등록"),
    VOTE_CREATED("20", "투표 생성"),
    VOTE_CLOSED("21", "투표 마감"),
    POST_CREATED("22", "게시글 등록"),
    COMMENT_CREATED("23", "댓글 등록"),
    MAINTENANCE_FEE_CREATED("24", "관리비 발생"),
    SYSTEM("99", "시스템");

    private final String code;
    private final String value;

    public static NotificationType from(String input) {
        // 이벤트 payload와 API 요청은 name/code/value 중 하나로 들어와도 허용한다
        return Arrays.stream(values())
                .filter(type -> type.name().equals(input)
                        || type.getCode().equals(input)
                        || type.getValue().equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown notification type: " + input));
    }

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<NotificationType> {
        public CodeConverter() {
            // notification.type은 enum name이 아니라 code로 저장한다
            super(NotificationType.class);
        }
    }
}
