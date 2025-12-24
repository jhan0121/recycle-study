package com.recyclestudy.member.controller;

import com.recyclestudy.exception.BadRequestException;
import com.recyclestudy.exception.DeviceActivationExpiredException;
import com.recyclestudy.exception.NotFoundException;
import com.recyclestudy.member.controller.request.DeviceDeleteRequest;
import com.recyclestudy.member.domain.DeviceIdentifier;
import com.recyclestudy.member.domain.Email;
import com.recyclestudy.member.service.MemberService;
import io.restassured.RestAssured;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DevicePageControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("이미 인증된 디바이스 인증 시도 시 400 응답을 반환한다")
    void authenticateDevice_AlreadyAuthenticated() {
        // given
        final String email = "test@test.com";
        final String identifier = "device-identifier";

        doThrow(new BadRequestException("이미 인증되었습니다"))
                .when(memberService).authenticateDevice(any(Email.class), any(DeviceIdentifier.class));

        // when & then
        given()
                .param("email", email)
                .param("identifier", identifier)
                .when()
                .get("/api/v1/device/auth")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("이미 인증되었습니다"));
    }

    @Test
    @DisplayName("인증 시 유효하지 않은 이메일 형식인 경우 400 응답을 반환한다")
    void authenticateDevice_InvalidEmailFormat() {
        // given
        final String invalidEmail = "invalid-email";
        final String identifier = "device-identifier";

        // when & then
        given()
                .param("email", invalidEmail)
                .param("identifier", identifier)
                .when()
                .get("/api/v1/device/auth")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("유효하지 않은 이메일 형식입니다."));
    }

    @Test
    @DisplayName("인증 유효 시간이 만료된 경우 400 응답을 반환한다")
    void authenticateDevice_Expired() {
        // given
        final String email = "test@test.com";
        final String identifier = "device-identifier";

        doThrow(new DeviceActivationExpiredException("인증 유효 시간이 만료되었습니다."))
                .when(memberService).authenticateDevice(any(Email.class), any(DeviceIdentifier.class));

        // when & then
        given()
                .param("email", email)
                .param("identifier", identifier)
                .when()
                .get("/api/v1/device/auth")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("인증 유효 시간이 만료되었습니다."));
    }

    @Test
    @DisplayName("디바이스 삭제 시 204 응답을 반환한다")
    void deleteDevice() {
        // given
        final DeviceDeleteRequest request = new DeviceDeleteRequest("test@test.com", "device-id");

        doNothing().when(memberService).deleteDevice(any());

        // when & then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .delete("/api/v1/device")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 디바이스 삭제 시 404 응답을 반환한다")
    void deleteDevice_NotFound() {
        // given
        final DeviceDeleteRequest request = new DeviceDeleteRequest("test@test.com", "not-existed");

        doThrow(new NotFoundException("존재하지 않는 디바이스 아이디입니다"))
                .when(memberService).deleteDevice(any());

        // when & then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .delete("/api/v1/device")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("존재하지 않는 디바이스 아이디입니다"));
    }

    @Test
    @DisplayName("소유자가 아닌 디바이스 삭제 시 400 응답을 반환한다")
    void deleteDevice_NotOwner() {
        // given
        final DeviceDeleteRequest request = new DeviceDeleteRequest("other@test.com", "device-id");

        doThrow(new BadRequestException("디바이스 소유자가 아닙니다."))
                .when(memberService).deleteDevice(any());

        // when & then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .delete("/api/v1/device")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("디바이스 소유자가 아닙니다."));
    }
}
