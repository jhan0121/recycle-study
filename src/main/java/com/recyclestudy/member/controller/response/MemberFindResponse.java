package com.recyclestudy.member.controller.response;

import com.recyclestudy.member.service.output.MemberFindOutput;
import java.time.LocalDateTime;
import java.util.List;

public record MemberFindResponse(List<MemberFindElement> devices) {

    public static MemberFindResponse from(final MemberFindOutput output) {
        final List<MemberFindElement> memberFindElements = output.elements().stream()
                .map(outputElement -> new MemberFindElement(outputElement.email().getValue(),
                        outputElement.createdAt()))
                .toList();
        return new MemberFindResponse(memberFindElements);
    }

    private record MemberFindElement(String email, LocalDateTime createdAt) {
    }
}
