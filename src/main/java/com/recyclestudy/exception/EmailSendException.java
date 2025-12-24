package com.recyclestudy.exception;

public class EmailSendException extends RuntimeException {

    public EmailSendException(final String message) {
        super(message);
    }

    public EmailSendException(String message, final Exception e) {
        super(message);
    }
}
