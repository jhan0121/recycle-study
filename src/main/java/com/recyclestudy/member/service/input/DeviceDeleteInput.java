package com.recyclestudy.member.service.input;

import com.recyclestudy.member.domain.DeviceIdentifier;
import com.recyclestudy.member.domain.Email;

public record DeviceDeleteInput(Email email, DeviceIdentifier deviceIdentifier,
                                DeviceIdentifier targetDeviceIdentifier) {

    public static DeviceDeleteInput from(final String emailValue, final String identifier,
                                         final String targetIdentifier) {
        final Email email = Email.from(emailValue);
        final DeviceIdentifier deviceIdentifier = DeviceIdentifier.from(identifier);
        final DeviceIdentifier targetDeviceIdentifier = DeviceIdentifier.from(targetIdentifier);
        return new DeviceDeleteInput(email, deviceIdentifier, targetDeviceIdentifier);
    }
}
