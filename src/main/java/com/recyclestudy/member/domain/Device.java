package com.recyclestudy.member.domain;

import com.recyclestudy.common.BaseEntity;
import com.recyclestudy.common.NullValidator;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    public static Device withoutId(final Member member, final DeviceIdentifier deviceIdentifier) {
        NullValidator.builder()
                .add(Fields.member, member)
                .add(Fields.identifier, deviceIdentifier)
                .validate();
        return new Device(member, deviceIdentifier);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "identifier", nullable = false))
    private DeviceIdentifier identifier;
}
