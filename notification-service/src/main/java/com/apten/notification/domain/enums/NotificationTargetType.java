package com.apten.notification.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationTargetType implements EnumMapperType {

    // 알림 클릭 시 어떤 도메인 상세 화면으로 이동할지 구분하는 대상 유형
    FACILITY_RESERVATION("01", "시설 예약"),
    GX_PROGRAM("02", "GX 프로그램"),
    USER("03", "사용자"),
    HOUSEHOLD("04", "세대"),
    VEHICLE("05", "차량"),
    VISITOR_VEHICLE("06", "방문 차량"),
    NOTICE("07", "공지사항"),
    VOTE("08", "투표"),
    POST("09", "게시글"),
    COMMENT("10", "댓글"),
    MAINTENANCE_FEE("11", "관리비"),
    SYSTEM("99", "시스템");

    private final String code;
    private final String value;

    public static NotificationTargetType from(String input) {
        // 내부 이벤트와 API에서 name/code/value 중 어떤 값이 와도 같은 대상 유형으로 해석한다
        return Arrays.stream(values())
                .filter(type -> type.name().equals(input)
                        || type.getCode().equals(input)
                        || type.getValue().equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown notification target type: " + input));
    }

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<NotificationTargetType> {
        public CodeConverter() {
            // notification.target_type은 enum name이 아니라 code로 저장한다
            super(NotificationTargetType.class);
        }
    }
}
