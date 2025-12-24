package com.recyclestudy.review.domain;

import com.recyclestudy.common.NullValidator;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class ReviewURL {

    private String value;

    public static ReviewURL from(final String value) {
        validateNotNull(value);
        return new ReviewURL(value);
    }

    private static void validateNotNull(final String value) {
        NullValidator.builder()
                .add(Fields.value, value)
                .validate();
    }
}
