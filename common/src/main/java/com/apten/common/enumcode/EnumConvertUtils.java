package com.apten.common.enumcode;

import java.util.Arrays;

// JPA converter가 DB code와 enum 객체를 서로 바꿀 때 사용하는 공통 유틸이다.
public final class EnumConvertUtils {

    // 인스턴스 생성을 막아 정적 변환 메서드만 사용하게 한다.
    private EnumConvertUtils() {
    }

    // DB에서 읽은 code에 맞는 enum 상수를 찾아 엔티티 필드로 복원한다.
    public static <E extends Enum<E> & EnumMapperType> E ofCode(Class<E> enumClass, String code) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(enumValue -> enumValue.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 enum code가 없습니다. enum="
                        + enumClass.getSimpleName() + ", code=" + code));
    }

    // 엔티티 enum 값을 DB에 저장할 code 문자열로 변환한다.
    public static <E extends Enum<E> & EnumMapperType> String toCode(E enumValue) {
        return enumValue.getCode();
    }
}
