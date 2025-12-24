package com.recyclestudy.review.controller.request;

import com.recyclestudy.review.service.input.ReviewSaveInput;

public record ReviewSaveRequest(String identifier, String targetUrl) {

    public ReviewSaveInput toInput() {
        return ReviewSaveInput.of(this.identifier, this.targetUrl);
    }
}
