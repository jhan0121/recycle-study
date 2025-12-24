package com.recyclestudy.review.repository;

import com.recyclestudy.review.domain.ReviewCycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCycleRepository extends JpaRepository<ReviewCycle, Long> {
}
