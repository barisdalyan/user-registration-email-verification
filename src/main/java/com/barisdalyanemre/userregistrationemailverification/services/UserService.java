package com.barisdalyanemre.userregistrationemailverification.services;

import com.barisdalyanemre.userregistrationemailverification.dtos.ResponseDto;
import com.barisdalyanemre.userregistrationemailverification.dtos.TokenResponseDto;
import com.barisdalyanemre.userregistrationemailverification.dtos.UserRequestDto;
import com.barisdalyanemre.userregistrationemailverification.dtos.UserResponseDto;
import com.barisdalyanemre.userregistrationemailverification.exceptions.EmailAlreadyInUseException;
import com.barisdalyanemre.userregistrationemailverification.exceptions.UserNotFoundException;
import com.barisdalyanemre.userregistrationemailverification.models.User;
import com.barisdalyanemre.userregistrationemailverification.repositories.ConfirmationTokenRepository;
import com.barisdalyanemre.userregistrationemailverification.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MESSAGE = "User with email %s not found";
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, email)));
    }

    public ResponseDto<List<UserResponseDto>> getUsers() {
        List<User> userList = userRepository.findAll();
        List<UserResponseDto> users = new ArrayList<>();
        userList.forEach(user -> users.add(convertToDto(user)));
        return new ResponseDto<>(HttpStatus.OK.value(), "Users retrieved successfully", users, LocalDateTime.now());
    }

    public ResponseDto<UserResponseDto> getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new ResponseDto<>(HttpStatus.OK.value(), "User retrieved successfully", convertToDto(user), LocalDateTime.now());
    }

    public ResponseDto<UserResponseDto> updateUser(Long id, UserRequestDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());

        String encodedPassword = bCryptPasswordEncoder.encode(userDto.password());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return new ResponseDto<>(HttpStatus.OK.value(), "User updated successfully", convertToDto(user), LocalDateTime.now());
    }

    public ResponseDto<Void> deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        confirmationTokenRepository.deleteAll(confirmationTokenRepository.findAllByUser(user));
        userRepository.deleteById(id);
        return new ResponseDto<>(HttpStatus.NO_CONTENT.value(), "User deleted successfully", null, LocalDateTime.now());
    }

    public TokenResponseDto signUpUser(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        boolean userExists = optionalUser.isPresent();

        if (userExists) {
            User requestedUser = optionalUser.get();
            if (requestedUser.getEnabled()) {
                throw new EmailAlreadyInUseException("Email already in use");
            }
            return confirmationTokenService.saveConfirmationToken(requestedUser);
        }

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return confirmationTokenService.saveConfirmationToken(user);
    }

    public void enableUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    private UserResponseDto convertToDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserRole().name(),
                user.getEnabled()
        );
    }
}
