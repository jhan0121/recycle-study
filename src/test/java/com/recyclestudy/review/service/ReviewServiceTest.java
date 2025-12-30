package com.recyclestudy.review.service;

import com.recyclestudy.exception.UnauthorizedException;
import com.recyclestudy.member.domain.ActivationExpiredDateTime;
import com.recyclestudy.member.domain.Device;
import com.recyclestudy.member.domain.DeviceIdentifier;
import com.recyclestudy.member.domain.Email;
import com.recyclestudy.member.domain.Member;
import com.recyclestudy.member.repository.DeviceRepository;
import com.recyclestudy.review.domain.NotificationStatus;
import com.recyclestudy.review.domain.Review;
import com.recyclestudy.review.domain.ReviewCycle;
import com.recyclestudy.review.domain.ReviewURL;
import com.recyclestudy.review.repository.ReviewCycleRepository;
import com.recyclestudy.review.repository.ReviewRepository;
import com.recyclestudy.review.service.input.ReviewSaveInput;
import com.recyclestudy.review.service.output.ReviewSaveOutput;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ReviewCycleRepository reviewCycleRepository;

    @Mock
    DeviceRepository deviceRepository;

    @Spy
    Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @InjectMocks
    ReviewService reviewService;

    LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now(clock);
    }

    @Test
    @DisplayName("리뷰와 리뷰 주기를 저장한다")
    void saveReview() {
        // given
        final String identifier = "device-id";
        final String urlValue = "https://test.com";
        final ReviewSaveInput input = ReviewSaveInput.of(identifier, urlValue);

        final Device device = Device.withoutId(
                Member.withoutId(Email.from("test@test.com")),
                DeviceIdentifier.from(identifier),
                true,
                ActivationExpiredDateTime.create(now)
        );

        final Email email = Email.from("test@test.com");
        final Member member = Member.withoutId(email);
        final Review review = Review.withoutId(member, ReviewURL.from(urlValue));
        final ReviewCycle cycle = ReviewCycle.withoutId(review, now.plusDays(1), NotificationStatus.PENDING);

        given(deviceRepository.findByIdentifier(any())).willReturn(Optional.of(device));
        given(reviewRepository.save(any(Review.class))).willReturn(review);
        given(reviewCycleRepository.saveAll(anyList())).willReturn(List.of(cycle));

        // when
        final ReviewSaveOutput actual = reviewService.saveReview(input);

        // then
        assertThat(actual.url()).isEqualTo(ReviewURL.from(urlValue));
        assertThat(actual.scheduledAts()).hasSize(1);
        verify(deviceRepository).findByIdentifier(any());
        verify(reviewRepository).save(any(Review.class));
        verify(reviewCycleRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("존재하지 않는 디바이스 아이디일 경우 예외를 던진다")
    void saveReview_fail_notFoundDevice() {
        // given
        final ReviewSaveInput input = ReviewSaveInput.of("not-found", "https://test.com");
        given(deviceRepository.findByIdentifier(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.saveReview(input))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("유효하지 않은 디바이스입니다");
    }

    @Test
    @DisplayName("활성화되지 않은 디바이스일 경우 예외를 던진다")
    void saveReview_fail_inactiveDevice() {
        // given
        final String identifier = "inactive-device";
        final ReviewSaveInput input = ReviewSaveInput.of(identifier, "https://test.com");

        final Device inactiveDevice = Device.withoutId(
                Member.withoutId(Email.from("test@test.com")),
                DeviceIdentifier.from(identifier),
                false,
                ActivationExpiredDateTime.create(now)
        );

        given(deviceRepository.findByIdentifier(any())).willReturn(Optional.of(inactiveDevice));

        // when & then
        assertThatThrownBy(() -> reviewService.saveReview(input))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("인증되지 않은 디바이스입니다");
    }
}
