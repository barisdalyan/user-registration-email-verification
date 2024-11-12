package com.barisdalyanemre.userregistrationemailverification.services;

import com.barisdalyanemre.userregistrationemailverification.dtos.TokenResponseDto;
import com.barisdalyanemre.userregistrationemailverification.exceptions.TokenNotFoundException;
import com.barisdalyanemre.userregistrationemailverification.exceptions.TokenStillValidException;
import com.barisdalyanemre.userregistrationemailverification.models.ConfirmationToken;
import com.barisdalyanemre.userregistrationemailverification.models.User;
import com.barisdalyanemre.userregistrationemailverification.repositories.ConfirmationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    private TokenResponseDto createAndSaveConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        confirmationTokenRepository.save(confirmationToken);

        return new TokenResponseDto(
                HttpStatus.CREATED.value(),
                "Token created successfully",
                token,
                LocalDateTime.now()
        );
    }

    public TokenResponseDto saveConfirmationToken(User user) {
        List<ConfirmationToken> confirmationTokenList = confirmationTokenRepository.findAllByUser(user);

        if (!confirmationTokenList.isEmpty()) {
            ConfirmationToken lastToken = confirmationTokenList.get(confirmationTokenList.size() - 1);

            if (lastToken.getExpiresAt().isAfter(LocalDateTime.now())) {
                throw new TokenStillValidException("Token is still valid: " + lastToken.getToken());
            }
        }

        return createAndSaveConfirmationToken(user);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token)
                .or(() -> {
                    throw new TokenNotFoundException("Token not found");
                });
    }

    public void setConfirmedAt(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
    }
}
