package com.recyclestudy.member.controller.request;

import com.recyclestudy.member.service.input.MemberSaveInput;

public record MemberSaveRequest(String email) {

    public MemberSaveInput toInput() {
        return MemberSaveInput.from(this.email);
    }
}
