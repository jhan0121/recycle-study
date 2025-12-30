package com.recyclestudy.review.repository;

import com.recyclestudy.review.domain.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
}
