package com.recyclestudy.email;

import com.recyclestudy.member.domain.Email;
import com.recyclestudy.review.domain.ReviewURL;
import com.recyclestudy.review.service.NotificationHistoryService;
import com.recyclestudy.review.service.ReviewCycleService;
import com.recyclestudy.review.service.output.ReviewSendOutput;
import com.recyclestudy.review.service.output.ReviewSendOutput.ReviewSendElement;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewEmailSenderTest {

    @Mock
    EmailSender emailSender;

    @Mock
    TemplateEngine templateEngine;

    @Mock
    ReviewCycleService reviewCycleService;

    @Mock
    NotificationHistoryService notificationHistoryService;

    @Spy
    Clock clock = Clock.fixed(Instant.parse("2025-01-01T08:00:00Z"), ZoneId.of("UTC"));

    @InjectMocks
    ReviewEmailSender reviewEmailSender;

    @Test
    @DisplayName("복습 대상자에게 메일을 발송한다")
    void sendReviewMail_success() {
        // given
        final Email targetEmail = Email.from("user@test.com");
        final List<Long> reviewCycleIds = List.of(1L, 2L);
        final List<ReviewURL> targetUrls = List.of(
                ReviewURL.from("https://example.com/article1"),
                ReviewURL.from("https://example.com/article2")
        );
        final ReviewSendElement element = ReviewSendElement.of(targetEmail, reviewCycleIds, targetUrls);
        final ReviewSendOutput output = new ReviewSendOutput(List.of(element));

        given(reviewCycleService.findTargetReviewCycle(any())).willReturn(output);
        given(templateEngine.process(eq("review_email"), any(Context.class))).willReturn("<html>복습 목록</html>");

        // when
        reviewEmailSender.sendReviewMail();

        // then
        verify(emailSender).send(
                eq("user@test.com"),
                eq("[Recycle Study] 오늘의 복습 목록이 도착했습니다"),
                eq("<html>복습 목록</html>")
        );
    }

    @Test
    @DisplayName("여러 대상자에게 각각 메일을 발송한다")
    void sendReviewMail_multipleRecipients() {
        // given
        final ReviewSendElement element1 = ReviewSendElement.of(
                Email.from("user1@test.com"),
                List.of(1L),
                List.of(ReviewURL.from("https://example.com/1"))
        );
        final ReviewSendElement element2 = ReviewSendElement.of(
                Email.from("user2@test.com"),
                List.of(2L),
                List.of(ReviewURL.from("https://example.com/2"))
        );
        final ReviewSendOutput output = new ReviewSendOutput(List.of(element1, element2));

        given(reviewCycleService.findTargetReviewCycle(any())).willReturn(output);
        given(templateEngine.process(eq("review_email"), any(Context.class))).willReturn("<html></html>");

        // when
        reviewEmailSender.sendReviewMail();

        // then
        verify(emailSender, times(2)).send(any(), any(), any());
        verify(emailSender).send(eq("user1@test.com"), any(), any());
        verify(emailSender).send(eq("user2@test.com"), any(), any());
    }

    @Test
    @DisplayName("복습 대상이 없으면 메일을 발송하지 않는다")
    void sendReviewMail_noRecipients() {
        // given
        final ReviewSendOutput output = new ReviewSendOutput(List.of());

        given(reviewCycleService.findTargetReviewCycle(any())).willReturn(output);

        // when
        reviewEmailSender.sendReviewMail();

        // then
        verify(emailSender, never()).send(any(), any(), any());
    }

    @Test
    @DisplayName("템플릿에 복습 URL 목록이 전달된다")
    void sendReviewMail_templateReceivesUrls() {
        // given
        final List<Long> reviewCycleIds = List.of(1L, 2L);
        final List<ReviewURL> targetUrls = List.of(
                ReviewURL.from("https://example.com/article1"),
                ReviewURL.from("https://example.com/article2")
        );
        final ReviewSendElement element = ReviewSendElement.of(Email.from("user@test.com"), reviewCycleIds, targetUrls);
        final ReviewSendOutput output = new ReviewSendOutput(List.of(element));
        final ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);

        given(reviewCycleService.findTargetReviewCycle(any())).willReturn(output);
        given(templateEngine.process(eq("review_email"), any(Context.class))).willReturn("<html></html>");

        // when
        reviewEmailSender.sendReviewMail();

        // then
        verify(templateEngine).process(eq("review_email"), contextCaptor.capture());

        final Context capturedContext = contextCaptor.getValue();
        final List<ReviewURL> capturedUrls = (List<ReviewURL>) capturedContext.getVariable("targetUrls");

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(capturedUrls).hasSize(2);
            softAssertions.assertThat(capturedUrls.getFirst().getValue()).isEqualTo("https://example.com/article1");
            softAssertions.assertThat(capturedUrls.get(1).getValue()).isEqualTo("https://example.com/article2");
        });
    }
}
