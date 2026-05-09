package com.apten.common.enumcode;

import com.fasterxml.jackson.annotation.JsonValue;

// DB 저장용 code와 API 응답용 value를 분리하기 위한 enum 공통 규약이다.
public interface EnumMapperType {

    // DB 컬럼에 저장할 짧고 안정적인 문자열 code를 반환한다.
    String getCode();

    // FE 응답 JSON에 보여줄 사용자 친화적인 value를 반환한다.
    @JsonValue
    String getValue();
}
