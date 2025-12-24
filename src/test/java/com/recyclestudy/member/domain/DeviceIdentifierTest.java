package com.recyclestudy.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeviceIdentifierTest {

    @Test
    @DisplayName("from 메서드를 활용하여 DeviceIdentifier를 생성할 수 있다")
    void from() {
        // given
        final String value = "test";

        // when
        final DeviceIdentifier actual = DeviceIdentifier.from(value);

        // then
        assertThat(actual.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("null 값으로 생성 시도 시, 예외를 발생한다")
    void throwExceptionWhenNull() {
        // given
        // when
        // then
        assertThatThrownBy(() -> DeviceIdentifier.from(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
