package com.recyclestudy.member;

import com.recyclestudy.member.domain.DeviceIdentifier;
import com.recyclestudy.member.domain.Email;
import com.recyclestudy.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/device")
@RequiredArgsConstructor
public class DeviceController {

    private final MemberService memberService;

    @GetMapping("/auth")
    public String authenticateDevice(
            @RequestParam("email") String email,
            @RequestParam("device") String deviceIdentifier
    ) {
        memberService.authenticateDevice(Email.from(email), DeviceIdentifier.from(deviceIdentifier));
        return "auth_success";
    }
}
