package com.recyclestudy.member.domain;

import com.recyclestudy.common.BaseEntity;
import com.recyclestudy.common.NullValidator;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@Getter
public class Member extends BaseEntity {

    public static Member withoutId(final Email email) {
        validateNotNull(email);
        return new Member(email);
    }

    private static void validateNotNull(final Email email) {
        NullValidator.builder()
                .add(Fields.email, email)
                .validate();
    }

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false))
    private Email email;
}
