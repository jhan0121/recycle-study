package com.recyclestudy.member.service.input;

import com.recyclestudy.member.domain.Email;

public record MemberSaveInput(Email email) {

    public static MemberSaveInput from(final String email) {
        final Email value = Email.from(email);
        return new MemberSaveInput(value);
    }
}
