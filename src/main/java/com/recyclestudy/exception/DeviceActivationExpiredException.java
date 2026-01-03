package com.recyclestudy.exception;

public class DeviceActivationExpiredException extends UnauthorizedException {
    public DeviceActivationExpiredException(String message) {
        super(message);
    }
}
