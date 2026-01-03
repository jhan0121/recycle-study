package com.recyclestudy.member.service.output;

import com.recyclestudy.member.domain.Device;
import com.recyclestudy.member.domain.DeviceIdentifier;
import com.recyclestudy.member.domain.Email;

public record MemberSaveOutput(Email email, DeviceIdentifier identifier) {

    public static MemberSaveOutput from(final Device device) {
        return new MemberSaveOutput(device.getMember().getEmail(), device.getIdentifier());
    }
}
