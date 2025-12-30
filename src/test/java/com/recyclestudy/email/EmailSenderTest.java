package com.recyclestudy.email;

import com.recyclestudy.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSenderTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailSender emailSender;

    @Test
    @DisplayName("메일을 성공적으로 발송한다")
    void send_success() {
        // given
        final String to = "test@test.com";
        final String subject = "테스트 제목";
        final String content = "<html>테스트 내용</html>";
        final MimeMessage mimeMessage = mock(MimeMessage.class);

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        emailSender.send(to, subject, content);

        // then
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("메일 발송 실패 시 EmailSendException을 던진다")
    void send_fail_throwsException() throws MessagingException {
        // given
        final String to = "test@test.com";
        final String subject = "테스트 제목";
        final String content = "<html>테스트 내용</html>";
        final MimeMessage mimeMessage = mock(MimeMessage.class);

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
        willThrow(new MessagingException("메일 서버 오류"))
                .given(mimeMessage).setRecipient(any(), any());

        // when & then
        assertThatThrownBy(() -> emailSender.send(to, subject, content))
                .isInstanceOf(EmailSendException.class)
                .hasMessage("메일 전송 중 오류가 발생했습니다.");
    }
}
