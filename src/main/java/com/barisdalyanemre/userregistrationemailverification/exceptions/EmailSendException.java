package com.barisdalyanemre.userregistrationemailverification.exceptions;

public class EmailSendException extends RuntimeException {

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
