package com.recyclestudy.review.service.input;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReviewSendInput(LocalDateTime scheduledAt) {

    public static ReviewSendInput from(final LocalDate targetDate, final LocalTime targetTime) {
        return new ReviewSendInput(LocalDateTime.of(targetDate, targetTime));
    }
}
