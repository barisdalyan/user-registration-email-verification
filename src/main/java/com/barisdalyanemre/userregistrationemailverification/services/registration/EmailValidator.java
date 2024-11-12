package com.barisdalyanemre.userregistrationemailverification.services.registration;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
import java.net.InetAddress;

@Service
public class EmailValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public boolean isValid(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }
        return isDomainValid(email);
    }

    private boolean isDomainValid(String email) {
        try {
            String domain = email.substring(email.indexOf('@') + 1);
            InetAddress.getByName(domain);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
