package com.barisdalyanemre.userregistrationemailverification.services.registration;

import com.barisdalyanemre.userregistrationemailverification.dtos.EmailConfirmationResponseDto;
import com.barisdalyanemre.userregistrationemailverification.dtos.UserRequestDto;
import com.barisdalyanemre.userregistrationemailverification.dtos.TokenResponseDto;
import com.barisdalyanemre.userregistrationemailverification.exceptions.InvalidEmailException;
import com.barisdalyanemre.userregistrationemailverification.exceptions.TokenExpiredException;
import com.barisdalyanemre.userregistrationemailverification.exceptions.TokenNotFoundException;
import com.barisdalyanemre.userregistrationemailverification.models.User;
import com.barisdalyanemre.userregistrationemailverification.models.UserRole;
import com.barisdalyanemre.userregistrationemailverification.services.UserService;
import com.barisdalyanemre.userregistrationemailverification.services.email.EmailService;
import com.barisdalyanemre.userregistrationemailverification.models.ConfirmationToken;
import com.barisdalyanemre.userregistrationemailverification.services.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;

    public TokenResponseDto register(UserRequestDto request) {
        boolean isValidEmail = emailValidator.isValid(request.email());

        if (!isValidEmail) {
            throw new InvalidEmailException("Invalid email");
        }

        TokenResponseDto response = userService.signUpUser(new User(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                UserRole.USER));

        if (response.status() == HttpStatus.CREATED.value() || response.status() == HttpStatus.OK.value()) {
            String token = response.token();
            String link = "http://localhost:8080/api/v1/registration/confirm?token=" + token;
            emailService.send(request.email(), buildEmail(request.firstName(), link));
        }

        return response;
    }

    public EmailConfirmationResponseDto confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new InvalidEmailException("Token already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableUser(confirmationToken.getUser().getEmail());

        return new EmailConfirmationResponseDto(HttpStatus.OK.value(), "Email confirmed successfully", LocalDateTime.now());
    }

    private String buildEmail(String name, String link) {
        return "<!doctype html>" +
                "<html lang=\"en\">" +
                "  <head>" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                "    <title>Email Verification Message</title>" +
                "    <style>" +
                "      @media all {" +
                "        .btn-primary table td:hover { background-color: #ec0867 !important; }" +
                "        .btn-primary a:hover { background-color: #ec0867 !important; border-color: #ec0867 !important; }" +
                "      }" +
                "      @media only screen and (max-width: 640px) {" +
                "        .main p, .main td, .main span { font-size: 16px !important; }" +
                "        .wrapper { padding: 8px !important; }" +
                "        .container { padding: 0 !important; padding-top: 8px !important; width: 100% !important; }" +
                "        .main { border-left-width: 0 !important; border-radius: 0 !important; border-right-width: 0 !important; }" +
                "        .btn table { max-width: 100% !important; width: 100% !important; }" +
                "        .btn a { font-size: 16px !important; max-width: 100% !important; width: 100% !important; }" +
                "      }" +
                "    </style>" +
                "  </head>" +
                "  <body style=\"font-family: Helvetica, sans-serif; font-size: 16px; background-color: #f4f5f6; margin: 0; padding: 0;\">" +
                "    <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#f4f5f6\">" +
                "      <tr>" +
                "        <td>&nbsp;</td>" +
                "        <td class=\"container\" width=\"600\" style=\"max-width: 600px; padding: 24px; margin: 0 auto;\">" +
                "          <div class=\"content\" style=\"max-width: 600px;\">" +
                "            <span class=\"preheader\" style=\"color: transparent; display: none; height: 0; max-height: 0; max-width: 0; opacity: 0; overflow: hidden;\">Thank you for registering. Verify your email to activate your account.</span>" +
                "            <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"main\" width=\"100%\" style=\"background: #ffffff; border: 1px solid #eaebed; border-radius: 16px;\">" +
                "              <tr>" +
                "                <td class=\"wrapper\" style=\"padding: 24px;\">" +
                "                  <h1 style=\"font-size: 24px; font-weight: bold; color: #0b0c0c;\">Verify Your Email</h1>" +
                "                  <p style=\"margin: 0 0 16px;\">Hi " + name + ",</p>" +
                "                  <p style=\"margin: 0 0 16px;\">Thanks for signing up! Just click the button below to verify your email and activate your account:</p>" +
                "                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"btn btn-primary\" width=\"100%\">" +
                "                    <tr>" +
                "                      <td align=\"left\" style=\"padding-bottom: 16px;\">" +
                "                        <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
                "                          <tr>" +
                "                            <td align=\"center\" bgcolor=\"#0867ec\" style=\"border-radius: 4px;\">" +
                "                              <a href=\"" + link + "\" target=\"_blank\" style=\"display: inline-block; font-size: 16px; font-weight: bold; padding: 12px 24px; color: #ffffff; text-decoration: none; background-color: #0867ec; border-radius: 4px;\">Verify Email</a>" +
                "                            </td>" +
                "                          </tr>" +
                "                        </table>" +
                "                      </td>" +
                "                    </tr>" +
                "                  </table>" +
                "                  <p style=\"margin: 0 0 16px;\">The link will expire in 15 minutes.</p>" +
                "                  <p style=\"margin: 0;\">Best regards,<br>The Team</p>" +
                "                </td>" +
                "              </tr>" +
                "            </table>" +
                "          </div>" +
                "        </td>" +
                "        <td>&nbsp;</td>" +
                "      </tr>" +
                "    </table>" +
                "  </body>" +
                "</html>";
    }
}
