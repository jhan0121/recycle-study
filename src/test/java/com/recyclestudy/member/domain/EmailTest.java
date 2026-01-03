package com.recyclestudy.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("from 메서드를 활용하여 Email을 생성할 수 있다")
    void from() {
        // given
        final String value = "test@test.com";

        // when
        final Email actual = Email.from(value);

        // then
        assertThat(actual.getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("null 값으로 생성 시도 시, 예외를 발생한다")
    void throwExceptionWhenNull(final String emptyValue) {
        // given
        // when
        // then
        assertThatThrownBy(() -> Email.from(emptyValue))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "valid@example.com",
            "another.email@sub.domain.co.kr",
            "test-user_name+tag@domain.net",
            "12345@test.org"
    })
    @DisplayName("올바른 형식인 값으로 생성 시도 시, Email을 생성할 수 있다")
    void from_validEmail_createsEmail(final String validValue) {
        // given
        // when
        final Email actual = Email.from(validValue);

        // then
        assertThat(actual.getValue()).isEqualTo(validValue);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "testtest.com",         // '@' 없음
            "test@",                // 도메인 없음
            "@test.com",            // 로컬 파트 없음
            "test@test",            // 최상위 도메인 없음
            "test@test.c",          // 최상위 도메인 너무 짧음
            "test@test.commmmmmmm", // 최상위 도메인 너무 김
            "te st@test.com",       // 로컬 파트에 공백
            "test@test com"         // 도메인에 공백
    })
    @DisplayName("맞지 않는 형식인 값으로 생성 시도 시, 예외를 발생한다")
    void throwExceptionWhenInvalidFormat(final String invalidValue) {
        //given
        //when
        //then
        assertThatThrownBy(() -> Email.from(invalidValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 이메일 형식입니다.");
    }

    @ParameterizedTest
    @CsvSource({
            "a@test.com, a*@test.com",
            "ab@test.com, a*@test.com",
            "abc@test.com, a**@test.com",
            "john@test.com, jo**@test.com",
            "hello@test.com, he***@test.com",
            "longname@test.com, lon*****@test.com",
            "test1234@test.com, tes*****@test.com"
    })
    @DisplayName("toMaskedValue 메서드를 활용하여 이메일을 마스킹할 수 있다")
    void toMaskedValue(final String originValue, final String expectedValue) {
        // given
        final Email email = Email.from(originValue);

        // when
        final String actual = email.toMaskedValue();

        // then
        assertThat(actual).isEqualTo(expectedValue);
    }
}
