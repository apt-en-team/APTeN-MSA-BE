package com.apten.apartmentcomplex.infrastructure.config;

import com.apten.apartmentcomplex.infrastructure.client.AuthFeignErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

// AuthInternalFeignClient 전용 Feign 설정 (@Configuration 제외 — 전역 적용 방지)
public class AuthFeignConfig {

    // Auth API 오류 디코더 등록
    @Bean
    public ErrorDecoder errorDecoder() {
        return new AuthFeignErrorDecoder();
    }
}
