package com.recyclestudy.review.service.output;

import com.recyclestudy.member.domain.Email;
import com.recyclestudy.review.domain.ReviewCycle;
import com.recyclestudy.review.domain.ReviewURL;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewSendOutput(List<ReviewSendElement> elements) {

    public static ReviewSendOutput from(final List<ReviewCycle> reviewCycles) {
        final List<ReviewSendElement> elements = reviewCycles.stream()
                .collect(Collectors.groupingBy(
                        cycle -> cycle.getReview().getMember().getEmail(),
                        Collectors.toUnmodifiableList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    final List<ReviewCycle> cycles = entry.getValue();
                    final List<Long> cycleIds = cycles.stream()
                            .map(ReviewCycle::getId)
                            .toList();
                    final List<ReviewURL> urls = cycles.stream()
                            .map(cycle -> cycle.getReview().getUrl())
                            .toList();
                    return ReviewSendElement.of(entry.getKey(), cycleIds, urls);
                })
                .toList();

        return new ReviewSendOutput(elements);
    }

    public record ReviewSendElement(
            Email email,
            List<Long> reviewCycleIds,
            List<ReviewURL> targetUrls
    ) {
        public static ReviewSendElement of(
                final Email email,
                final List<Long> reviewCycleIds,
                final List<ReviewURL> targetUrls
        ) {
            return new ReviewSendElement(email, reviewCycleIds, targetUrls);
        }
    }
}
