package com.recyclestudy.email;

import com.recyclestudy.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender javaMailSender;

    public void send(final String to, final String subject, final String content) {
        try {
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(mimeMessage);

            log.info("메일 발송 성공: to={}", to);

        } catch (MessagingException e) {
            log.error("메일 전송 실패. to={}", to, e);
            throw new EmailSendException("메일 전송 중 오류가 발생했습니다.", e);
        }
    }
}
