package com.recyclestudy.member.domain;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class DeviceTest {

    @Test
    @DisplayName("withoutId 메서드를 통해 Device를 생성할 수 있다")
    void withoutId() {
        // given
        final Email email = Email.from("test@test.com");
        final Member member = Member.withoutId(email);
        final DeviceIdentifier deviceIdentifier = DeviceIdentifier.from("test");

        // when
        final Device actual = Device.withoutId(member, deviceIdentifier);

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getMember().getEmail()).isEqualTo(email);
            softAssertions.assertThat(actual.getIdentifier()).isEqualTo(deviceIdentifier);
        });
    }

    @ParameterizedTest
    @MethodSource("provideInvalidValue")
    @DisplayName("null로 생성 시도 시, 예외를 던진다")
    void throwExceptionWhenNull(final Member member, final DeviceIdentifier deviceIdentifier) {
        //given
        //when
        //then
        assertThatThrownBy(() -> Device.withoutId(member, deviceIdentifier))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> provideInvalidValue() {
        final Email email = Email.from("test@test.com");
        final Member member = Member.withoutId(email);

        final DeviceIdentifier deviceIdentifier = DeviceIdentifier.from("test");

        return Stream.of(
                Arguments.of(member, null),
                Arguments.of(null, deviceIdentifier),
                Arguments.of(null, null)
        );
    }
}
