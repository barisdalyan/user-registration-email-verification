package com.barisdalyanemre.userregistrationemailverification.services.email;

public interface EmailSender {
    void send(String to, String email);
}
