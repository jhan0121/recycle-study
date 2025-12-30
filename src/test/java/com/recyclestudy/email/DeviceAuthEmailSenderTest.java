package com.recyclestudy.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeviceAuthEmailSenderTest {

    @Mock
    private EmailSender emailSender;

    @Mock
    private TemplateEngine templateEngine;

    private DeviceAuthEmailSender deviceAuthEmailSender;

    @BeforeEach
    void setUp() {
        deviceAuthEmailSender = new DeviceAuthEmailSender(emailSender, templateEngine);
        ReflectionTestUtils.setField(deviceAuthEmailSender, "baseUrl", "https://example.com");
    }

    @Test
    @DisplayName("디바이스 인증 메일을 발송한다")
    void sendDeviceAuthMail_success() {
        // given
        final String email = "test@test.com";
        final String deviceId = "device-123";
        final String expectedHtml = "<html>인증 링크</html>";

        given(templateEngine.process(eq("auth_email"), any(Context.class))).willReturn(expectedHtml);

        // when
        deviceAuthEmailSender.sendDeviceAuthMail(email, deviceId);

        // then
        verify(emailSender).send(
                eq(email),
                eq("[Recycle Study] 디바이스 인증을 완료해주세요."),
                eq(expectedHtml)
        );
    }

    @Test
    @DisplayName("올바른 인증 URL이 템플릿에 전달된다")
    void sendDeviceAuthMail_correctAuthUrl() {
        // given
        final String email = "test@test.com";
        final String deviceId = "device-123";
        final ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);

        given(templateEngine.process(eq("auth_email"), any(Context.class))).willReturn("<html></html>");

        // when
        deviceAuthEmailSender.sendDeviceAuthMail(email, deviceId);

        // then
        verify(templateEngine).process(eq("auth_email"), contextCaptor.capture());

        final Context capturedContext = contextCaptor.getValue();
        final String authUrl = (String) capturedContext.getVariable("authUrl");

        assertThat(authUrl).isEqualTo("https://example.com/api/v1/device/auth?email=test@test.com&identifier=device-123");
    }
}
