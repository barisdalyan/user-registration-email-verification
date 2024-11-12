package com.barisdalyanemre.userregistrationemailverification.dtos;

public record UserRequestDto(
        String firstName,
        String lastName,
        String email,
        String password
) {}
