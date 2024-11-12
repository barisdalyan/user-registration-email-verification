package com.barisdalyanemre.userregistrationemailverification.dtos;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String userRole,
        Boolean enabled
) {}
