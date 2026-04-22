package com.apten.auth.infrastructure.mapper;

import com.apten.auth.application.model.dto.AuthDto;
import java.util.List;

// auth-service의 복잡 조회를 MyBatis로 분리할 때 사용할 Mapper 인터페이스 골격
// JPA로 처리하지 않는 인증 조회가 생기면 이 인터페이스와 XML을 함께 확장한다
public interface AuthQueryMapper {

    // 인증 사용자 목록 조회가 필요해질 때 확장할 기본 메서드 위치
    List<AuthDto> findUserSummaries();
}
