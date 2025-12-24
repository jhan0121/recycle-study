package com.recyclestudy.member.domain;

import com.recyclestudy.common.NullValidator;
import com.recyclestudy.exception.DeviceActivationExpiredException;
import jakarta.persistence.Embeddable;
import java.time.Duration;
import java.time.LocalDateTime;
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
public class ActivationExpiredDateTime {

    private static final Duration EXPIRE_TIME_RATE = Duration.ofMinutes(5);

    public static ActivationExpiredDateTime create(final LocalDateTime currentTime) {
        validateNotNull(currentTime);
        return new ActivationExpiredDateTime(currentTime.plusMinutes(EXPIRE_TIME_RATE.toMinutes()));
    }

    private static void validateNotNull(final LocalDateTime currentTime) {
        NullValidator.builder()
                .add(Fields.value, currentTime)
                .validate();
    }

    private LocalDateTime value;

    public void checkExpired(final LocalDateTime currentTime) {
        if (currentTime.isAfter(value)) {
            throw new DeviceActivationExpiredException("인증 유효 시간이 만료되었습니다.");
        }
    }
}
