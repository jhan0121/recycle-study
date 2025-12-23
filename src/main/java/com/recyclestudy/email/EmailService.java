package com.recyclestudy.email;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
            final String authUrl = baseUrl + "/api/v1/device/auth?email=" + email + "&identifier=" + deviceId;

            final Context context = new Context();
            context.setVariable("authUrl", authUrl);

            final String message = templateEngine.process("auth_email", context);

            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[Recycle Study] 디바이스 인증을 완료해주세요.");
            mimeMessageHelper.setText(message, true);
            javaMailSender.send(mimeMessage);
        } catch (final Exception e) {
            throw new RuntimeException("메일 전송에 실패했습니다.", e);
        }
    }
}
