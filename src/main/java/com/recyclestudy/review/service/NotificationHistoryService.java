package com.recyclestudy.review.service;

import com.recyclestudy.review.domain.NotificationHistory;
import com.recyclestudy.review.domain.NotificationStatus;
import com.recyclestudy.review.domain.ReviewCycle;
import com.recyclestudy.review.repository.NotificationHistoryRepository;
import com.recyclestudy.review.repository.ReviewCycleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationHistoryService {

    private final NotificationHistoryRepository notificationHistoryRepository;
    private final ReviewCycleRepository reviewCycleRepository;

    @Transactional
    public void saveAll(final List<Long> reviewCycleIds, final NotificationStatus status) {
        final List<ReviewCycle> reviewCycles = reviewCycleRepository.findAllById(reviewCycleIds);

        final List<NotificationHistory> histories = reviewCycles.stream()
                .map(cycle -> NotificationHistory.withoutId(cycle, status))
                .toList();

        notificationHistoryRepository.saveAll(histories);
        log.info("[NOTIFY_HIST_UPDATED] 알림 이력 상태 변경: status={}, count={}", status, histories.size());
    }
}
