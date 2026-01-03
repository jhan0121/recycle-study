package com.recyclestudy.member.controller.response;

import com.recyclestudy.member.service.output.MemberSaveOutput;

public record MemberSaveResponse(String email, String identifier) {

    public static MemberSaveResponse from(final MemberSaveOutput output) {
        return new MemberSaveResponse(output.email().getValue(), output.identifier().getValue());
    }
}
