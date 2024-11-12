package com.barisdalyanemre.userregistrationemailverification.controllers;

import com.barisdalyanemre.userregistrationemailverification.dtos.ResponseDto;
import com.barisdalyanemre.userregistrationemailverification.dtos.UserResponseDto;
import com.barisdalyanemre.userregistrationemailverification.dtos.UserRequestDto;
import com.barisdalyanemre.userregistrationemailverification.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<ResponseDto<List<UserResponseDto>>> getUsers() {
        ResponseDto<List<UserResponseDto>> response = userService.getUsers();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.status()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUser(@PathVariable Long id) {
        ResponseDto<UserResponseDto> response = userService.getUser(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.status()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDto userDto) {
        ResponseDto<UserResponseDto> response = userService.updateUser(id, userDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteUser(@PathVariable Long id) {
        ResponseDto<Void> response = userService.deleteUser(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.status()));
    }
}
