package com.recyclestudy.email;

import com.recyclestudy.member.domain.Email;
import com.recyclestudy.review.domain.NotificationStatus;
import com.recyclestudy.review.domain.ReviewURL;
import com.recyclestudy.review.service.NotificationHistoryService;
import com.recyclestudy.review.service.ReviewCycleService;
import com.recyclestudy.review.service.input.ReviewSendInput;
import com.recyclestudy.review.service.output.ReviewSendOutput;
import com.recyclestudy.review.service.output.ReviewSendOutput.ReviewSendElement;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewEmailSender {

    private final EmailSender emailSender;
    private final TemplateEngine templateEngine;
    private final ReviewCycleService reviewCycleService;
    private final NotificationHistoryService notificationHistoryService;
    private final Clock clock;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendReviewMail() {

        final LocalDate targetDate = LocalDate.now(clock);
        final LocalTime targetTime = LocalTime.of(8, 0);

        final ReviewSendOutput targetReviewCycle = reviewCycleService.findTargetReviewCycle(
                ReviewSendInput.from(targetDate, targetTime));

        final List<ReviewSendElement> elements = targetReviewCycle.elements();
        log.info("복습 메일 발송 시작: 대상 {}명", elements.size());

        int successCount = 0;
        int failCount = 0;

        for (final ReviewSendElement element : elements) {
            final String message = createMessage(element.targetUrls());
            final Email targetEmail = element.email();

            final boolean success = sendToTargetEmail(targetEmail, message);

            if (success) {
                notificationHistoryService.saveAll(element.reviewCycleIds(), NotificationStatus.SENT);
                successCount++;
            } else {
                notificationHistoryService.saveAll(element.reviewCycleIds(), NotificationStatus.FAILED);
                failCount++;
            }
        }

        log.info("복습 메일 발송 처리 완료: 성공 {}명, 실패 {}명", successCount, failCount);
    }

    private boolean sendToTargetEmail(final Email targetEmail, final String message) {
        try {
            emailSender.send(targetEmail.getValue(), "[Recycle Study] 오늘의 복습 목록이 도착했습니다", message);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    private String createMessage(final List<ReviewURL> targetUrls) {
        final Context context = new Context();
        context.setVariable("targetUrls", targetUrls);
        return templateEngine.process("review_email", context);
    }
}
