package com.apten.apartmentcomplex.infrastructure.client;

import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminCreateReq;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminCreateRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// Auth Service 내부 호출 예시용 클라이언트이다.
@Component
@RequiredArgsConstructor
public class AuthInternalClient {

    // 향후 내부 인증 호출이 필요해지면 RestClient 기반으로 연결한다.
    private final RestClient.Builder restClientBuilder;

    public InternalAdminCreateRes createAdmin(InternalAdminCreateReq req) {
        // TODO: 단지 등록 후 최초 관리자 생성 예정
        // TODO: 관리자 생성 시 Auth Service 내부 호출 예정
        // TODO: Kafka Outbox 구조는 기존 이벤트 발행 흐름을 그대로 유지한다.
        throw new UnsupportedOperationException("TODO: Auth Service 내부 호출은 다음 작업에서 연결합니다.");
    }
}
