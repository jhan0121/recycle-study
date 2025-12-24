package com.recyclestudy.review.service.output;

import com.recyclestudy.review.domain.ReviewURL;
import java.time.LocalDateTime;
import java.util.List;

public record ReviewSaveOutput(ReviewURL url, List<LocalDateTime> scheduledAts) {

    public static ReviewSaveOutput of(ReviewURL url, List<LocalDateTime> scheduledAts) {
        return new ReviewSaveOutput(url, scheduledAts);
    }
}
