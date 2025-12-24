package com.recyclestudy.review.domain;

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
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@Getter
public class Review extends BaseEntity {

    public static Review withoutId(final ReviewURL url) {
        validateNotNull(url);
        return new Review(url);
    }

    private static void validateNotNull(final ReviewURL url) {
        NullValidator.builder()
                .add(Fields.url, url)
                .validate();
    }

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "url", nullable = false, columnDefinition = "TEXT"))
    private ReviewURL url;
}
