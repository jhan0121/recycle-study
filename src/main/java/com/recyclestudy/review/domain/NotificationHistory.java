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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "notification_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@Getter
public class NotificationHistory extends BaseEntity {

    public static NotificationHistory withoutId(
            final ReviewCycle reviewCycle,
            final NotificationStatus status
    ) {
        validateNotNull(reviewCycle, status);
        return new NotificationHistory(reviewCycle, status);
    }

    private static void validateNotNull(
            final ReviewCycle reviewCycle,
            final NotificationStatus status
    ) {
        NullValidator.builder()
                .add(Fields.reviewCycle, reviewCycle)
                .add(Fields.status, status)
                .validate();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_cycle_id", nullable = false)
    private ReviewCycle reviewCycle;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;
}
