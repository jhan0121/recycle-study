package com.recyclestudy.review.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewCycleDurationTest {

    @Test
    @DisplayName("특정 날짜와 시간을 기준으로 5가지 주기를 계산할 수 있다")
    void calculate_with_time() {
        // given
        final LocalDate target = LocalDate.of(2025, 1, 1);
        final LocalTime time = LocalTime.of(10, 0);

        // when
        final List<LocalDateTime> actual = ReviewCycleDuration.calculate(target, time);

        // then
        assertThat(actual).containsExactly(
                LocalDateTime.of(2025, 1, 2, 10, 0),    // DAY (1)
                LocalDateTime.of(2025, 1, 8, 10, 0),    // WEEK (7)
                LocalDateTime.of(2025, 1, 31, 10, 0),   // MONTH (30)
                LocalDateTime.of(2025, 4, 1, 10, 0),    // QUARTER (90)
                LocalDateTime.of(2025, 6, 30, 10, 0)   // HALF_YEAR (180)
        );
    }

    @Test
    @DisplayName("날짜만 주어졌을 때 기본 시간(08:00)으로 5가지 주기를 계산한다")
    void calculate_without_time() {
        // given
        final LocalDate target = LocalDate.of(2025, 1, 1);

        // when
        final List<LocalDateTime> actual = ReviewCycleDuration.calculate(target);

        // then
        assertThat(actual).containsExactly(
                LocalDateTime.of(2025, 1, 2, 8, 0),
                LocalDateTime.of(2025, 1, 8, 8, 0),
                LocalDateTime.of(2025, 1, 31, 8, 0),
                LocalDateTime.of(2025, 4, 1, 8, 0),
                LocalDateTime.of(2025, 6, 30, 8, 0)
        );
    }
}
