package com.apten.notification.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationCategory implements EnumMapperType {

    // 알림 생성 전 사용자의 ON/OFF 설정을 확인하는 단위
    RESERVATION("01", "시설 예약"),
    GX("02", "GX"),
    VEHICLE("03", "차량"),
    SIGNUP("04", "회원가입"),
    NOTICE("05", "공지"),
    VOTE("06", "투표"),
    BOARD("07", "게시판"),
    COMMENT("08", "댓글"),
    MAINTENANCE("09", "관리비");

    private final String code;
    private final String value;

    public static NotificationCategory from(String input) {
        // API 요청은 enum name, code, value 중 하나로 들어와도 같은 category로 해석한다
        return Arrays.stream(values())
                .filter(category -> category.name().equals(input)
                        || category.getCode().equals(input)
                        || category.getValue().equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown notification category: " + input));
    }

    public static NotificationCategory fromType(NotificationType type) {
        // 알림 type별로 어떤 사용자 설정 category를 확인할지 결정한다
        return switch (type) {
            case FACILITY_REMINDER -> RESERVATION;
            case GX_MINIMUM_REACHED, GX_APPROVAL_REMINDER, GX_APPROVED, GX_REJECTED -> GX;
            case VEHICLE_REGISTRATION_REQUESTED, VEHICLE_REGISTRATION_APPROVED,
                    VEHICLE_REGISTRATION_REJECTED, VEHICLE_ENTRY, VEHICLE_EXIT,
                    VISITOR_VEHICLE_ENTRY, VISITOR_VEHICLE_EXIT -> VEHICLE;
            case SIGNUP_REQUESTED, SIGNUP_APPROVED, SIGNUP_REJECTED -> SIGNUP;
            case NOTICE_CREATED -> NOTICE;
            case VOTE_CREATED, VOTE_CLOSED -> VOTE;
            case POST_CREATED -> BOARD;
            case COMMENT_CREATED -> COMMENT;
            case MAINTENANCE_FEE_CREATED -> MAINTENANCE;
            case SYSTEM -> null;
        };
    }

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<NotificationCategory> {
        public CodeConverter() {
            // notification_setting.category는 enum name이 아니라 code로 저장한다
            super(NotificationCategory.class);
        }
    }
}
