package com.recyclestudy.review.domain;

import com.recyclestudy.common.BaseEntity;
import com.recyclestudy.common.NullValidator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "cycle")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@Getter
public class ReviewCycle extends BaseEntity {

    public static ReviewCycle withoutId(
            final Review review,
            final LocalDateTime scheduledAt,
            final NotificationStatus status
    ) {
        validateNotNull(review, scheduledAt, status);
        return new ReviewCycle(review, scheduledAt, status);
    }

    private static void validateNotNull(
            final Review review,
            final LocalDateTime scheduledAt,
            final NotificationStatus status
    ) {
        NullValidator.builder()
                .add(Fields.review, review)
                .add(Fields.scheduledAt, scheduledAt)
                .add(Fields.status, status)
                .validate();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    private LocalDateTime scheduledAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;
}
