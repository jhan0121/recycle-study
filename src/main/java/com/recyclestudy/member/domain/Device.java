package com.recyclestudy.member.domain;

import com.recyclestudy.common.BaseEntity;
import com.recyclestudy.common.NullValidator;
import com.recyclestudy.exception.BadRequestException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "device")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@Getter
public class Device extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "identifier", nullable = false, unique = true))
    private DeviceIdentifier identifier;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "activation_expires_at", nullable = false))
    private ActivationExpiredDateTime activationExpiresAt;

    public static Device withoutId(
            final Member member,
            final DeviceIdentifier deviceIdentifier,
            final boolean isActive,
            final ActivationExpiredDateTime activationExpiresAt
    ) {
        NullValidator.builder()
                .add(Fields.member, member)
                .add(Fields.identifier, deviceIdentifier)
                .add(Fields.activationExpiresAt, activationExpiresAt)
                .validate();
        return new Device(member, deviceIdentifier, isActive, activationExpiresAt);
    }

    public void activate(final LocalDateTime currentTime) {
        activationExpiresAt.checkExpired(currentTime);
        this.isActive = true;
    }

    public void verifyOwner(final Email email) {
        if (!member.hasEmail(email)) {
            throw new BadRequestException("디바이스 소유자가 아닙니다.");
        }
    }
}
