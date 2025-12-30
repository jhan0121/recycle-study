package com.recyclestudy.review.domain;

import com.recyclestudy.member.domain.Email;
import com.recyclestudy.member.domain.Member;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewTest {

    private static Stream<Arguments> provideInvalidValue() {
        final Email email = Email.from("test@test.com");
        final Member member = Member.withoutId(email);
        final ReviewURL reviewURL = ReviewURL.from("https://test.com");

        return Stream.of(
                Arguments.of(null, reviewURL),
                Arguments.of(member, null)
        );
    }

    @Test
    @DisplayName("Review를 생성할 수 있다")
    void withoutId() {
        // given
        final Email email = Email.from("test@test.com");
        final Member member = Member.withoutId(email);
        final ReviewURL url = ReviewURL.from("https://test.com");

        // when
        final Review actual = Review.withoutId(member, url);

        // then
        assertThat(actual.getUrl()).isEqualTo(url);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidValue")
    @DisplayName("null로 생성 시도 시, 예외를 던진다")
    void throwExceptionWhenNull(final Member member, final ReviewURL url) {
        // given
        // when
        // then
        assertThatThrownBy(() -> Review.withoutId(member, url))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
