package com.recyclestudy.member.domain;

import com.recyclestudy.common.NullValidator;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
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
public class Email {

    private static final String EMAIL_FORMAT = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_FORMAT);

    private String value;

    public static Email from(final String value) {
        validateNotNull(value);
        validateEmailFormat(value);
        return new Email(value);
    }

    private static void validateNotNull(final String value) {
        NullValidator.builder()
                .add(Fields.value, value)
                .validate();
    }

    private static void validateEmailFormat(final String emailValue) {
        if (!EMAIL_PATTERN.matcher(emailValue).matches()) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }
    }

    public String toMaskedValue() {
        final String[] split = value.split("@");
        final String maskedLocalPart = maskLocalPart(split[0]);
        return maskedLocalPart + "@" + split[1];
    }

    private String maskLocalPart(final String localPart) {
        int length = localPart.length();

        if (length <= 2) {
            return localPart.charAt(0) + "*";
        }

        int visibleLength = Math.min(3, length / 2);
        String visiblePart = localPart.substring(0, visibleLength);
        String maskedPart = "*".repeat(length - visibleLength);

        return visiblePart + maskedPart;
    }
}
