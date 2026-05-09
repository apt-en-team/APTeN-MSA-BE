package com.apten.common.enumcode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 스캔된 enum 목록을 camelCase key 기준으로 저장하고 조회하는 registry이다.
public class EnumMapper {

    // enum 이름 key와 code/value 목록을 저장하는 내부 저장소이다.
    private final Map<String, List<EnumMapperValue>> enumValues = new HashMap<>();

    // enum key와 value 목록을 등록해 FE가 같은 기준으로 조회할 수 있게 한다.
    public void put(String key, List<EnumMapperValue> values) {
        enumValues.put(key, values);
    }

    // 특정 enum key에 해당하는 code/value 목록을 반환한다.
    public List<EnumMapperValue> get(String key) {
        return enumValues.getOrDefault(key, List.of());
    }

    // 등록된 전체 enum code/value 목록을 읽기 전용 Map으로 반환한다.
    public Map<String, List<EnumMapperValue>> getAll() {
        return Collections.unmodifiableMap(enumValues);
    }
}
