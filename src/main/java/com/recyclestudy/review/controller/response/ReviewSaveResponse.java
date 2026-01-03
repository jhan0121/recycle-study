package com.recyclestudy.review.controller.response;

import com.recyclestudy.review.domain.ReviewURL;
import java.time.LocalDateTime;
import java.util.List;

public record ReviewSaveResponse(String url, List<LocalDateTime> scheduledAts) {

    public static ReviewSaveResponse of(ReviewURL url, List<LocalDateTime> scheduledAts) {
        return new ReviewSaveResponse(url.getValue(), scheduledAts);
    }
}
