package com.apten.common.enumcode;

import jakarta.persistence.AttributeConverter;

// JPA가 enum 객체와 DB code 문자열을 자동 변환하도록 돕는 공통 추상 converter이다.
public abstract class AbstractEnumCodeConverter<E extends Enum<E> & EnumMapperType>
        implements AttributeConverter<E, String> {

    // 변환 대상 enum 클래스 타입이다.
    private final Class<E> enumClass;

    // null 값을 허용할지 여부를 각 enum converter에서 정한다.
    private final boolean nullable;

    // nullable을 허용하지 않는 기본 converter 생성자이다.
    protected AbstractEnumCodeConverter(Class<E> enumClass) {
        this(enumClass, false);
    }

    // enum 클래스와 null 허용 여부를 받아 변환 규칙을 만든다.
    protected AbstractEnumCodeConverter(Class<E> enumClass, boolean nullable) {
        this.enumClass = enumClass;
        this.nullable = nullable;
    }

    // 엔티티 enum 값을 DB에 저장할 code 문자열로 변환한다.
    @Override
    public String convertToDatabaseColumn(E attribute) {
        if (attribute == null) {
            return handleNull();
        }
        return EnumConvertUtils.toCode(attribute);
    }

    // DB에서 읽은 code 문자열을 엔티티 enum 값으로 복원한다.
    @Override
    public E convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            handleNull();
            return null;
        }
        return EnumConvertUtils.ofCode(enumClass, dbData);
    }

    // null을 허용하지 않는 enum 필드에 null이 들어오면 빠르게 예외로 알려준다.
    private String handleNull() {
        if (nullable) {
            return null;
        }
        throw new IllegalArgumentException(enumClass.getSimpleName() + " code는 null일 수 없습니다.");
    }
}
