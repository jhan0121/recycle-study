package com.recyclestudy.review.controller;

import com.recyclestudy.review.controller.request.ReviewSaveRequest;
import com.recyclestudy.review.controller.response.ReviewSaveResponse;
import com.recyclestudy.review.service.ReviewService;
import com.recyclestudy.review.service.input.ReviewSaveInput;
import com.recyclestudy.review.service.output.ReviewSaveOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewSaveResponse> saveReview(
            @RequestHeader(value = "X-Device-Id", required = false) String headerIdentifier,
            @RequestBody ReviewSaveRequest request
    ) {
        final String resolvedIdentifier = getResolvedIdentifier(request.identifier(), headerIdentifier);

        final ReviewSaveInput input = ReviewSaveInput.of(resolvedIdentifier, request.targetUrl());
        final ReviewSaveOutput output = reviewService.saveReview(input);
        ReviewSaveResponse response = ReviewSaveResponse.of(output.url(), output.scheduledAts());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private String getResolvedIdentifier(final String identifier, final String headerIdentifier) {
        if (headerIdentifier == null) {
            return identifier;
        }
        return headerIdentifier;
    }
}
