package com.recyclestudy.member.domain;

import com.recyclestudy.common.NullValidator;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class DeviceIdentifier {

    public static DeviceIdentifier from(final String value) {
        validateNotNull(value);
        return new DeviceIdentifier(value);
    }

    private static void validateNotNull(final String value) {
        NullValidator.builder()
                .add(Fields.value, value)
                .validate();
    }

    private String value;
}
