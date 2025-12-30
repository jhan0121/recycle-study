package com.recyclestudy.review.repository;

import com.recyclestudy.review.domain.ReviewCycle;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCycleRepository extends JpaRepository<ReviewCycle, Long> {

    List<ReviewCycle> findAllByScheduledAt(LocalDateTime scheduledAt);
}
