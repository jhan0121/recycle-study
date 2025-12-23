package com.recyclestudy.member.controller;

import com.recyclestudy.email.EmailService;
import com.recyclestudy.exception.NotFoundException;
import com.recyclestudy.exception.UnauthorizedException;
import com.recyclestudy.member.controller.request.MemberSaveRequest;
import com.recyclestudy.member.domain.DeviceIdentifier;
import com.recyclestudy.member.domain.Email;
import com.recyclestudy.member.service.MemberService;
import com.recyclestudy.member.service.output.MemberFindOutput;
import com.recyclestudy.member.service.output.MemberSaveOutput;
import io.restassured.RestAssured;
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
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("이메일을 통해 새로운 디바이스를 저장한다")
    void saveMember() {
        // given
        final String email = "test@test.com";
        final String identifier = "device-identifier";
        final MemberSaveRequest request = new MemberSaveRequest(email);
        final MemberSaveOutput output = new MemberSaveOutput(Email.from(email), DeviceIdentifier.from(identifier));

        given(memberService.saveDevice(any())).willReturn(output);

        // when & then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/members")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("email", equalTo(email))
                .body("identifier", equalTo(identifier));

        verify(emailService).sendDeviceAuthMail(email, identifier);
    }

    @Test
    @DisplayName("멤버의 모든 디바이스 정보를 조회한다")
    void findAllMemberDevices() {
        // given
        final String email = "test@test.com";
        final String identifier = "device-identifier";
        final MemberFindOutput output = MemberFindOutput.from(List.of());

        given(memberService.findAllMemberDevices(any())).willReturn(output);

        // when & then
        given()
                .param("email", email)
                .param("identifier", identifier)
                .when()
                .get("/api/v1/members")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("devices", hasSize(0));
    }

    @Test
    @DisplayName("존재하지 않는 멤버 조회 시 404 응답을 반환한다")
    void findAllMemberDevices_NotFoundMember() {
        // given
        final String email = "notfound@test.com";
        final String identifier = "device-identifier";

        given(memberService.findAllMemberDevices(any()))
                .willThrow(new NotFoundException("존재하지 않는 멤버입니다"));

        // when & then
        given()
                .param("email", email)
                .param("identifier", identifier)
                .when()
                .get("/api/v1/members")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("존재하지 않는 멤버입니다"));
    }

    @Test
    @DisplayName("인증되지 않은 디바이스로 조회 시 401 응답을 반환한다")
    void findAllMemberDevices_UnauthorizedDevice() {
        // given
        final String email = "test@test.com";
        final String identifier = "unauthorized-id";

        given(memberService.findAllMemberDevices(any()))
                .willThrow(new UnauthorizedException("인증되지 않은 디바이스입니다"));

        // when & then
        given()
                .param("email", email)
                .param("identifier", identifier)
                .when()
                .get("/api/v1/members")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo("인증되지 않은 디바이스입니다"));
    }

    @Test
    @DisplayName("조회 시 유효하지 않은 이메일 형식인 경우 400 응답을 반환한다")
    void findAllMemberDevices_InvalidEmailFormat() {
        // given
        final String invalidEmail = "invalid-email";
        final String identifier = "device-identifier";

        // when & then
        given()
                .param("email", invalidEmail)
                .param("identifier", identifier)
                .when()
                .get("/api/v1/members")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("유효하지 않은 이메일 형식입니다."));
    }

    @Test
    @DisplayName("이메일이 누락된 경우 400 응답을 반환한다")
    void saveMember_NullEmail() {
        // given
        final MemberSaveRequest request = new MemberSaveRequest(null);

        // when & then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/members")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("null이 될 수 없습니다: value"));
    }

    @Test
    @DisplayName("유효하지 않은 이메일 형식으로 요청 시 400 응답을 반환한다")
    void saveMember_InvalidEmailFormat() {
        // given
        final String invalidEmail = "invalid-email";
        final MemberSaveRequest request = new MemberSaveRequest(invalidEmail);

        // when & then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/members")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("유효하지 않은 이메일 형식입니다."));
    }
}
