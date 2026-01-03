package com.recyclestudy.review.controller;

import com.recyclestudy.exception.UnauthorizedException;
import com.recyclestudy.restdocs.APIBaseTest;
import com.recyclestudy.review.controller.request.ReviewSaveRequest;
import com.recyclestudy.review.domain.ReviewURL;
import com.recyclestudy.review.service.ReviewService;
import com.recyclestudy.review.service.output.ReviewSaveOutput;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

class ReviewControllerTest extends APIBaseTest {

    @MockitoBean
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰를 저장하면 201 응답을 반환한다")
    void saveReview() {
        // given
        final String identifier = "device-id";
        final String url = "https://test.com";
        final ReviewSaveRequest request = new ReviewSaveRequest(identifier, url);
        final ReviewSaveOutput output = ReviewSaveOutput.of(ReviewURL.from(url), List.of(LocalDateTime.now()));

        given(reviewService.saveReview(any())).willReturn(output);

        // when
        // then
        given(this.spec)
                .filter(document(DEFAULT_REST_DOC_PATH,
                        builder()
                                .tag("Review")
                                .summary("리뷰 저장")
                                .description("리뷰를 저장하면 201 응답을 반환한다")
                                .requestFields(
                                        fieldWithPath("identifier").type(JsonFieldType.STRING)
                                                .description("디바이스 식별자"),
                                        fieldWithPath("url").type(JsonFieldType.STRING)
                                                .description("리뷰할 URL")
                                )
                                .responseFields(
                                        fieldWithPath("url").type(JsonFieldType.STRING).description("리뷰할 URL"),
                                        fieldWithPath("scheduledAts").type(JsonFieldType.ARRAY)
                                                .description("복습 예정 일시 목록")
                                )
                ))
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

        // when
        // then
        given(this.spec)
                .filter(document(DEFAULT_REST_DOC_PATH,
                        builder()
                                .tag("Review")
                                .summary("리뷰 저장")
                                .description("유효하지 않은 디바이스로 리뷰 저장 시 401 응답을 반환한다")
                                .requestFields(
                                        fieldWithPath("identifier").type(JsonFieldType.STRING)
                                                .description("디바이스 식별자"),
                                        fieldWithPath("url").type(JsonFieldType.STRING)
                                                .description("리뷰할 URL")
                                )
                                .responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                                )
                ))
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

        // when
        // then
        given(this.spec)
                .filter(document(DEFAULT_REST_DOC_PATH,
                        builder()
                                .tag("Review")
                                .summary("리뷰 저장")
                                .description("인증되지 않은 디바이스로 리뷰 저장 시 401 응답을 반환한다")
                                .requestFields(
                                        fieldWithPath("identifier").type(JsonFieldType.STRING)
                                                .description("디바이스 식별자"),
                                        fieldWithPath("url").type(JsonFieldType.STRING)
                                                .description("리뷰할 URL")
                                )
                                .responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                                )
                ))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/reviews")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo("인증되지 않은 디바이스입니다"));
    }
}
