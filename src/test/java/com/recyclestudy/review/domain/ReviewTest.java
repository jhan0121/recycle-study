package com.recyclestudy.review.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewTest {

    @Test
    @DisplayName("Review를 생성할 수 있다")
    void withoutId() {
        // given
        final ReviewURL url = ReviewURL.from("https://test.com");

        // when
        final Review actual = Review.withoutId(url);

        // then
        assertThat(actual.getUrl()).isEqualTo(url);
    }

    @Test
    @DisplayName("null로 생성 시도 시, 예외를 던진다")
    void throwExceptionWhenNull() {
        // given
        // when & then
        assertThatThrownBy(() -> Review.withoutId(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
