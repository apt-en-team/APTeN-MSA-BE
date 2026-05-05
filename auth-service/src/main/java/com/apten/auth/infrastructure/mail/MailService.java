package com.apten.auth.infrastructure.mail;

import com.apten.auth.exception.AuthErrorCode;
import com.apten.common.exception.BusinessException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetMail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[아파트엔] 비밀번호 재설정 안내");

            // HTML 이메일 템플릿
            String html = """
                    <!DOCTYPE html>
                    <html>
                    <body style="margin:0;padding:0;background:#f0f4ff;font-family:'Apple SD Gothic Neo',sans-serif;">
                      <table width="100%%" cellpadding="0" cellspacing="0">
                        <tr>
                          <td align="center" style="padding:40px 16px;">
                            <table width="520" cellpadding="0" cellspacing="0" style="background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(73,115,229,0.1);">
                              
                              <!-- 헤더 -->
                              <tr>
                                <td style="background:#4973e5;padding:28px 32px;">
                                  <span style="color:#fff;font-size:20px;font-weight:700;">아파트엔 APTeN</span>
                                </td>
                              </tr>
                              
                              <!-- 본문 -->
                              <tr>
                                <td style="padding:36px 32px;">
                                  <h2 style="margin:0 0 12px;color:#1a202c;font-size:22px;font-weight:700;">비밀번호 재설정 안내</h2>
                                  <p style="margin:0 0 8px;color:#555;font-size:14px;line-height:1.7;">
                                    아래 버튼을 클릭하여 비밀번호를 재설정하세요.<br>
                                    링크는 <strong>30분</strong> 후 만료됩니다.
                                  </p>
                                  
                                  <!-- 버튼 -->
                                  <div style="margin:32px 0;text-align:center;">
                                    <a href="%s"
                                       style="display:inline-block;background:#4973e5;color:#fff;font-size:15px;font-weight:700;padding:14px 36px;border-radius:8px;text-decoration:none;">
                                      비밀번호 재설정하기
                                    </a>
                                  </div>
                                  
                                  <!-- 주의 문구 -->
                                  <p style="margin:0;color:#999;font-size:12px;line-height:1.6;">
                                    본인이 요청하지 않은 경우 이 메일을 무시하세요.<br>
                                    링크는 30분 후 자동으로 만료됩니다.
                                  </p>
                                </td>
                              </tr>
                              
                              <!-- 푸터 -->
                              <tr>
                                <td style="background:#f8faff;padding:20px 32px;border-top:1px solid #e8eef8;">
                                  <p style="margin:0;color:#aaa;font-size:12px;text-align:center;">
                                    © 2026 APTeN 아파트엔. All rights reserved.
                                  </p>
                                </td>
                              </tr>
                              
                            </table>
                          </td>
                        </tr>
                      </table>
                    </body>
                    </html>
                    """.formatted(resetLink);

            helper.setText(html, true); // true = HTML 모드
            mailSender.send(message);

        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.MAIL_SEND_FAILED);
        }
    }
}