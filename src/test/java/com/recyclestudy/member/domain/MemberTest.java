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
}
