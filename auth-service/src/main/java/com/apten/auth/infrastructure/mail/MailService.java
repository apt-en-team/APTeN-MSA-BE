package com.apten.auth.infrastructure.mail;

import com.apten.auth.exception.AuthErrorCode;
import com.apten.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetMail(String toEmail, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("[APTeN] 비밀번호 재설정 안내");
            message.setText(
                    "아래 링크를 클릭하여 비밀번호를 재설정하세요.\n\n"
                            + resetLink
                            + "\n\n링크는 30분간 유효합니다."
            );
            mailSender.send(message);
        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.MAIL_SEND_FAILED);
        }
    }
}