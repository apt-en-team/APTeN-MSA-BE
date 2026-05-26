package com.apten.notification.infrastructure.websocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class NotificationWebSocketSessionRegistry {

    // 한 사용자가 여러 탭이나 기기에서 접속할 수 있으므로 userId별 세션 묶음을 관리한다
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> sessionsByUserId = new ConcurrentHashMap<>();
    // 세션 종료 시 userId를 역조회해 빠르게 제거하기 위한 인덱스
    private final ConcurrentHashMap<String, Long> userIdBySessionId = new ConcurrentHashMap<>();

    public void register(Long userId, WebSocketSession session) {
        if (userId == null || session == null) {
            return;
        }
        // 같은 userId의 기존 세션은 유지하고 새 탭/기기 세션만 추가한다
        sessionsByUserId.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(session);
        userIdBySessionId.put(session.getId(), userId);
    }

    public List<WebSocketSession> findByUserId(Long userId) {
        // 전송 중 컬렉션 변경 영향을 피하려고 복사본을 반환한다
        Set<WebSocketSession> sessions = sessionsByUserId.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(sessions);
    }

    public boolean hasSession(Long userId) {
        Set<WebSocketSession> sessions = sessionsByUserId.get(userId);
        return sessions != null && !sessions.isEmpty();
    }

    public void remove(WebSocketSession session) {
        if (session == null) {
            return;
        }
        Long userId = userIdBySessionId.remove(session.getId());
        if (userId == null) {
            return;
        }

        // 사용자에게 열린 세션이 더 없으면 map entry도 정리한다
        Set<WebSocketSession> sessions = sessionsByUserId.get(userId);
        if (sessions == null) {
            return;
        }
        sessions.remove(session);
        if (sessions.isEmpty()) {
            sessionsByUserId.remove(userId, sessions);
        }
    }
}
