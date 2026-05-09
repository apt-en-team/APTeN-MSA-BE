package com.apten.common.enumcode;

// FE가 enum 목록을 조회할 때 사용할 code/value 한 쌍을 담는다.
public record EnumMapperValue(
        // DB 저장 기준과 매칭되는 enum code이다.
        String code,

        // 화면에 표시할 enum value이다.
        String value
) {
}
