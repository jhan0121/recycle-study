package com.recyclestudy.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("withoutId 메서드를 통해 Member를 생성할 수 있다")
    void withoutId() {
        // given
        final Email email = Email.from("test@test.com");

        // when
        final Member actual = Member.withoutId(email);

        // then
        assertThat(actual.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("null로 생성 시도 시, 예외를 던진다")
    void throwExceptionWhenNull() {
        //given
        //when
        //then
        assertThatThrownBy(() -> Member.withoutId(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("hasEmail 메서드를 통해 이메일 일치 여부를 확인할 수 있다")
    void hasEmail() {
        // given
        final Email email = Email.from("test@test.com");
        final Member member = Member.withoutId(email);

        // when
        final boolean actual = member.hasEmail(email);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("이메일이 일치하지 않으면 false를 반환한다")
    void hasEmail_fail() {
        // given
        final Email email = Email.from("test@test.com");
        final Member member = Member.withoutId(email);

        // when
        final boolean actual = member.hasEmail(Email.from("other@test.com"));

        // then
        assertThat(actual).isFalse();
    }
}
