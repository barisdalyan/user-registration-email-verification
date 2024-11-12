package com.barisdalyanemre.userregistrationemailverification.services.email;

import com.barisdalyanemre.userregistrationemailverification.exceptions.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Complete Registration");
            helper.setFrom("hello@barisdalyanemre.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailSendException("Failed to send email to " + to, e);
        }
    }
}
