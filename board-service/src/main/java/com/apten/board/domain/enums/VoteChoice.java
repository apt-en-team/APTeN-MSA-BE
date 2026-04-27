package com.apten.board.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 찬반 투표 선택값 enum이다.
@Getter
public enum VoteChoice implements EnumMapperType {

    // 찬성 선택이다.
    AGREE("01", "찬성"),

    // 반대 선택이다.
    DISAGREE("02", "반대");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답에 노출하는 표시값이다.
    private final String value;

    // 코드와 표시값을 함께 초기화한다.
    VoteChoice(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<VoteChoice> {

        // VoteChoice 전용 converter이다.
        public CodeConverter() {
            super(VoteChoice.class);
        }
    }
}
