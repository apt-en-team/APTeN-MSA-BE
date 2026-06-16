package com.apten.apartmentcomplex.infrastructure.client;

import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminCreateReq;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminCreateRes;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminDeleteRes;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminUpdateReq;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminUpdateRes;
import com.apten.apartmentcomplex.infrastructure.config.AuthFeignConfig;
import com.apten.common.response.ResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Auth 서비스 관리자 내부 API 클라이언트 (MSA 경계)
@FeignClient(
        name = "auth-internal",
        url = "${services.auth.url}",
        configuration = AuthFeignConfig.class
)
public interface AuthInternalFeignClient {

    // 관리자 계정 생성
    @PostMapping("/internal/auth/admins")
    ResultResponse<InternalAdminCreateRes> createAdmin(@RequestBody InternalAdminCreateReq req);

    // 관리자 계정 소프트 삭제
    @PatchMapping("/internal/auth/admins/{userId}/delete")
    ResultResponse<InternalAdminDeleteRes> softDeleteAdmin(@PathVariable("userId") Long userId);

    // 관리자 계정 수정
    @PatchMapping("/internal/auth/admins/{userId}")
    ResultResponse<InternalAdminUpdateRes> updateAdmin(@PathVariable("userId") Long userId, @RequestBody InternalAdminUpdateReq req);
}
