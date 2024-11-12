package com.barisdalyanemre.userregistrationemailverification.dtos;

import java.time.LocalDateTime;

public record ResponseDto<T>(
        int status,
        String message,
        T data,
        LocalDateTime timestamp
) {}
