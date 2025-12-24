package com.recyclestudy.review.controller;

import com.recyclestudy.exception.UnauthorizedException;
import com.recyclestudy.review.controller.request.ReviewSaveRequest;
import com.recyclestudy.review.domain.ReviewURL;
import com.recyclestudy.review.service.ReviewService;
import com.recyclestudy.review.service.output.ReviewSaveOutput;
import io.restassured.RestAssured;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("리뷰를 저장하면 201 응답을 반환한다")
    void saveReview() {
        // given
        final String identifier = "device-id";
        final String url = "https://test.com";
        final ReviewSaveRequest request = new ReviewSaveRequest(identifier, url);
        final ReviewSaveOutput output = ReviewSaveOutput.of(ReviewURL.from(url), List.of(LocalDateTime.now()));

        given(reviewService.saveReview(any())).willReturn(output);

        // when & then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/reviews")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("url", equalTo(url));
    }

    @Test
    @DisplayName("유효하지 않은 디바이스로 리뷰 저장 시 401 응답을 반환한다")
    void saveReview_Unauthorized() {
        // given
        final ReviewSaveRequest request = new ReviewSaveRequest("invalid-id", "https://test.com");

        given(reviewService.saveReview(any()))
                .willThrow(new UnauthorizedException("유효하지 않은 디바이스입니다"));

        // when & then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/reviews")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo("유효하지 않은 디바이스입니다"));
    }

    @Test
    @DisplayName("인증되지 않은 디바이스로 리뷰 저장 시 401 응답을 반환한다")
    void saveReview_InactiveDevice() {
        // given
        final ReviewSaveRequest request = new ReviewSaveRequest("inactive-id", "https://test.com");

        given(reviewService.saveReview(any()))
                .willThrow(new UnauthorizedException("인증되지 않은 디바이스입니다"));

        // when & then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/reviews")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo("인증되지 않은 디바이스입니다"));
    }
}
