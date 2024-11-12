package com.barisdalyanemre.userregistrationemailverification.dtos;

import java.time.LocalDateTime;

public record EmailConfirmationResponseDto(
        int status,
        String message,
        LocalDateTime timestamp
) {}
