package com.barisdalyanemre.userregistrationemailverification.controllers;

import com.barisdalyanemre.userregistrationemailverification.dtos.EmailConfirmationResponseDto;
import com.barisdalyanemre.userregistrationemailverification.dtos.UserRequestDto;
import com.barisdalyanemre.userregistrationemailverification.dtos.TokenResponseDto;
import com.barisdalyanemre.userregistrationemailverification.services.registration.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<TokenResponseDto> register(@RequestBody UserRequestDto request) {
        TokenResponseDto response = registrationService.register(request);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.status()));
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<EmailConfirmationResponseDto> confirm(@RequestParam("token") String token) {
        EmailConfirmationResponseDto response = registrationService.confirmToken(token);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.status()));
    }
}
