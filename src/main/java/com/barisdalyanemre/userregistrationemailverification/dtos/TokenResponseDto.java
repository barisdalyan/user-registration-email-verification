package com.barisdalyanemre.userregistrationemailverification.dtos;

import java.time.LocalDateTime;

public record TokenResponseDto(
        int status,
        String message,
        String token,
        LocalDateTime timestamp
) {}
