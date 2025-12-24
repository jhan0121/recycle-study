package com.recyclestudy.review.domain;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewCycleTest {

    @Test
    @DisplayName("ReviewCycle을 생성할 수 있다")
    void withoutId() {
        // given
        final Review review = Review.withoutId(ReviewURL.from("https://test.com"));
        final LocalDateTime scheduledAt = LocalDateTime.now();
        final NotificationStatus status = NotificationStatus.PENDING;

        // when
        final ReviewCycle actual = ReviewCycle.withoutId(review, scheduledAt, status);

        // then
        assertThat(actual.getReview()).isEqualTo(review);
        assertThat(actual.getScheduledAt()).isEqualTo(scheduledAt);
        assertThat(actual.getStatus()).isEqualTo(status);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidValue")
    @DisplayName("null로 생성 시도 시, 예외를 던진다")
    void throwExceptionWhenNull(final Review review, final LocalDateTime scheduledAt,
                                final NotificationStatus status) {
        // given
        // when & then
        assertThatThrownBy(() -> ReviewCycle.withoutId(review, scheduledAt, status))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> provideInvalidValue() {
        final Review review = Review.withoutId(ReviewURL.from("https://test.com"));
        final LocalDateTime scheduledAt = LocalDateTime.now();
        final NotificationStatus status = NotificationStatus.PENDING;

        return Stream.of(
                Arguments.of(null, scheduledAt, status),
                Arguments.of(review, null, status),
                Arguments.of(review, scheduledAt, null),
                Arguments.of(null, null, null)
        );
    }
}
