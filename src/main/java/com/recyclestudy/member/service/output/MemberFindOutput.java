package com.recyclestudy.member.service.output;

import com.recyclestudy.member.domain.Device;
import com.recyclestudy.member.domain.Email;
import java.time.LocalDateTime;
import java.util.List;

public record MemberFindOutput(List<MemberFindElement> elements) {

    public static MemberFindOutput from(final List<Device> devices) {
        final List<MemberFindElement> memberFindElements = devices.stream()
                .map(device -> new MemberFindElement(device.getMember().getEmail(), device.getCreatedAt()))
                .toList();
        return new MemberFindOutput(memberFindElements);
    }

    public record MemberFindElement(Email email, LocalDateTime createdAt) {
    }
}
