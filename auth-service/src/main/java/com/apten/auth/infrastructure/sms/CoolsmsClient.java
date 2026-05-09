package com.apten.auth.infrastructure.sms;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Coolsms SDK를 통한 SMS 발송 클라이언트
@Component
public class CoolsmsClient {

    private final DefaultMessageService messageService;

    // 발신번호 — application.yml에서 주입
    @Value("${coolsms.from}")
    private String from;

    // API Key, Secret으로 Coolsms 서비스 초기화
    public CoolsmsClient(
            @Value("${coolsms.api-key}") String apiKey,
            @Value("${coolsms.api-secret}") String apiSecret) {
        this.messageService = NurigoApp.INSTANCE.initialize(
                apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    // 인증번호 문자 발송
    public void send(String to, String code) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        message.setText("[APTeN] 인증번호: " + code + " (5분 이내 입력)");
        messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}