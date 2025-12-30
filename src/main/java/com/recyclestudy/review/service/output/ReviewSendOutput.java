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
                        Collectors.mapping(
                                cycle -> cycle.getReview().getUrl(),
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .map(entry -> ReviewSendElement.of(entry.getKey(), entry.getValue()))
                .toList();

        return new ReviewSendOutput(elements);
    }

    public record ReviewSendElement(Email email, List<ReviewURL> targetUrls) {
        public static ReviewSendElement of(final Email email, final List<ReviewURL> targetUrl) {
            return new ReviewSendElement(email, targetUrl);
        }
    }
}
