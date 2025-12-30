package com.recyclestudy.email;

import com.recyclestudy.member.domain.Email;
import com.recyclestudy.review.domain.ReviewURL;
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
import org.springframework.scheduling.annotation.Async;
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
    private final Clock clock;

    @Async
    @Scheduled(cron = "0 0 8 * * *")
    public void sendReviewMail() {

        final LocalDate targetDate = LocalDate.now(clock);
        final LocalTime targetTime = LocalTime.of(8, 0);

        final ReviewSendOutput targetReviewCycle = reviewCycleService.findTargetReviewCycle(
                ReviewSendInput.from(targetDate, targetTime));

        for (final ReviewSendElement element : targetReviewCycle.elements()) {
            final String message = createMessage(element.targetUrls());
            final Email targetEmail = element.email();

            emailSender.send(targetEmail.getValue(), "[Recycle Study] 오늘의 복습 목록이 도착했습니다", message);
            log.info("복습 메일 발송 성공: email={}", targetEmail);
        }
    }

    private String createMessage(final List<ReviewURL> targetUrls) {
        final Context context = new Context();
        context.setVariable("targetUrls", targetUrls);
        return templateEngine.process("review_email", context);
    }
}
