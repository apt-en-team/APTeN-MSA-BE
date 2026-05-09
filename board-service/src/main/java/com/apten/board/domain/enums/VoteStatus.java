package com.apten.board.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 투표 진행 상태 enum이다.
@Getter
public enum VoteStatus implements EnumMapperType {

    // 시작 전 상태이다.
    READY("01", "시작 전"),

    // 진행 중 상태이다.
    OPEN("02", "진행 중"),

    // 종료 상태이다.
    CLOSED("03", "종료");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답에 노출하는 표시값이다.
    private final String value;

    // 코드와 표시값을 함께 초기화한다.
    VoteStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<VoteStatus> {

        // VoteStatus 전용 converter이다.
        public CodeConverter() {
            super(VoteStatus.class);
        }
    }
}
