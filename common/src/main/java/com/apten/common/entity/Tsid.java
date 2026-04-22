package com.apten.common.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 모든 서비스 엔티티가 TSID 기반 식별자 규칙을 따른다는 표시용 어노테이션
// 실제 생성 전략이 정해지면 공통 식별자 설정과 함께 연결할 기준점이다
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tsid {
}
