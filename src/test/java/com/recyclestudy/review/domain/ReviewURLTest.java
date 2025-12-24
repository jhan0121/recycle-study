package com.recyclestudy.review.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewURLTest {

    @Test
    @DisplayName("ReviewURL을 생성할 수 있다")
    void from() {
        // given
        final String value = "https://test.com";

        // when
        final ReviewURL actual = ReviewURL.from(value);

        // then
        assertThat(actual.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("null로 생성 시도 시, 예외를 던진다")
    void throwExceptionWhenNull() {
        // given
        // when & then
        assertThatThrownBy(() -> ReviewURL.from(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
