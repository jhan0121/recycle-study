package com.recyclestudy.review.service;

import com.recyclestudy.exception.UnauthorizedException;
import com.recyclestudy.member.domain.Device;
import com.recyclestudy.member.repository.DeviceRepository;
import com.recyclestudy.review.domain.NotificationStatus;
import com.recyclestudy.review.domain.Review;
import com.recyclestudy.review.domain.ReviewCycle;
import com.recyclestudy.review.domain.ReviewCycleDuration;
import com.recyclestudy.review.repository.ReviewCycleRepository;
import com.recyclestudy.review.repository.ReviewRepository;
import com.recyclestudy.review.service.input.ReviewSaveInput;
import com.recyclestudy.review.service.output.ReviewSaveOutput;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewCycleRepository reviewCycleRepository;
    private final DeviceRepository deviceRepository;
    private final Clock clock;

    public ReviewSaveOutput saveReview(final ReviewSaveInput input) {
        final Device device = deviceRepository.findByIdentifier(input.identifier())
                .orElseThrow(() -> new UnauthorizedException("유효하지 않은 디바이스입니다"));
        checkValidDevice(device);

        final Review review = Review.withoutId(input.url());
        final Review savedReview = reviewRepository.save(review);

        final LocalDate current = LocalDate.now(clock);
        final List<LocalDateTime> scheduledAts = ReviewCycleDuration.calculate(current);

        final List<ReviewCycle> reviewCycles = scheduledAts.stream()
                .map(scheduledAt -> ReviewCycle.withoutId(review, scheduledAt, NotificationStatus.PENDING))
                .toList();

        final List<LocalDateTime> savedScheduledAts = reviewCycleRepository.saveAll(reviewCycles)
                .stream().map(ReviewCycle::getScheduledAt)
                .toList();
        return ReviewSaveOutput.of(savedReview.getUrl(), savedScheduledAts);
    }

    private static void checkValidDevice(final Device device) {
        if (!device.isActive()) {
            throw new UnauthorizedException("인증되지 않은 디바이스입니다");
        }
    }
}
