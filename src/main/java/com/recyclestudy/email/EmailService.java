package com.recyclestudy.email;

import com.recyclestudy.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${auth.base-url}")
    private String baseUrl;

    @Async
    public void sendDeviceAuthMail(String email, String deviceId) {
        try {
            final String authUrl = createAuthUrl(email, deviceId);
            final String message = createMessage(authUrl);

            sendMail(email, "[Recycle Study] 디바이스 인증을 완료해주세요.", message);

            log.info("인증 메일 발송 성공: {}", email);

        } catch (Exception e) {
            log.error("메일 전송 실패. email={}, deviceId={}", email, deviceId, e);

            throw new EmailSendException("메일 전송 중 오류가 발생했습니다.", e);
        }
    }

    private String createAuthUrl(String email, String deviceId) {
        return String.format("%s/api/v1/device/auth?email=%s&identifier=%s", baseUrl, email, deviceId);
    }

    private String createMessage(String authUrl) {
        final Context context = new Context();
        context.setVariable("authUrl", authUrl);
        return templateEngine.process("auth_email", context);
    }

    private void sendMail(String to, String subject, String content) throws MessagingException {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        javaMailSender.send(mimeMessage);
    }
}
