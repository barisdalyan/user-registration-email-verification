package com.barisdalyanemre.userregistrationemailverification.exceptions;

public class TokenStillValidException extends RuntimeException {

    public TokenStillValidException(String message) {
        super(message);
    }
}
