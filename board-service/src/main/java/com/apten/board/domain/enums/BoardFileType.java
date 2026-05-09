package com.apten.board.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 게시판 첨부파일 유형 enum이다.
@Getter
public enum BoardFileType implements EnumMapperType {

    // 이미지 파일 유형이다.
    IMAGE("01", "이미지 파일"),

    // 일반 파일 유형이다.
    FILE("02", "일반 파일");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답에 노출하는 표시값이다.
    private final String value;

    // 코드와 표시값을 함께 초기화한다.
    BoardFileType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<BoardFileType> {

        // BoardFileType 전용 converter이다.
        public CodeConverter() {
            super(BoardFileType.class);
        }
    }
}
