package com.recyclestudy.review.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public enum ReviewCycleDuration {
    DAY(Duration.ofDays(1)),
    WEEK(Duration.ofDays(7)),
    MONTH(Duration.ofDays(30)),
    QUARTER(Duration.ofDays(90)),
    HALF_YEAR(Duration.ofDays(180)),
    ;

    private final Duration duration;

    ReviewCycleDuration(final Duration duration) {
        this.duration = duration;
    }

    public static List<LocalDateTime> calculate(final LocalDate target, final LocalTime time) {
        return Arrays.stream(ReviewCycleDuration.values())
                .map(cycle -> target.plusDays(cycle.duration.toDays())
                        .atTime(time))
                .toList();
    }

    public static List<LocalDateTime> calculate(final LocalDate target) {
        return calculate(target, LocalTime.of(8, 0));
    }
}
