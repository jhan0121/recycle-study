package com.recyclestudy.review.domain;

import com.recyclestudy.common.BaseEntity;
import com.recyclestudy.common.NullValidator;
import jakarta.persistence.Column;
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
@Table(name = "review_cycle")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@Getter
public class ReviewCycle extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    public static ReviewCycle withoutId(final Review review, final LocalDateTime scheduledAt) {
        validateNotNull(review, scheduledAt);
        return new ReviewCycle(review, scheduledAt);
    }

    private static void validateNotNull(final Review review, final LocalDateTime scheduledAt) {
        NullValidator.builder()
                .add(Fields.review, review)
                .add(Fields.scheduledAt, scheduledAt)
                .validate();
    }
}
