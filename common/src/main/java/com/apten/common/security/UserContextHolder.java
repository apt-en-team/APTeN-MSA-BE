package com.apten.common.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// 요청 처리 중 현재 로그인 사용자 정보를 잠시 보관하는 ThreadLocal 저장소
// gateway에서 전달한 헤더를 읽은 뒤 서비스 계층 어디서나 같은 사용자 정보를 참조할 때 사용한다
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserContextHolder {

    // 현재 스레드 기준 사용자 컨텍스트 저장소
    private static final ThreadLocal<UserContext> USER_CONTEXT = new ThreadLocal<>();

    // 요청 시작 시 현재 사용자 정보를 ThreadLocal에 저장한다
    // 필터나 인터셉터에서 헤더를 읽은 뒤 가장 먼저 호출될 수 있는 메서드다
    public static void set(UserContext userContext) {
        USER_CONTEXT.set(userContext);
    }

    // 서비스나 하위 계층에서 현재 로그인 사용자를 꺼낼 때 사용한다
    // 메서드 파라미터로 계속 사용자 정보를 전달하지 않도록 돕는 접근 지점이다
    public static UserContext get() {
        return USER_CONTEXT.get();
    }

    // 요청 처리가 끝난 뒤 ThreadLocal에 남은 사용자 정보를 정리한다
    // 다음 요청으로 사용자 정보가 섞이지 않게 막는 마무리 단계다
    public static void clear() {
        USER_CONTEXT.remove();
    }
}
