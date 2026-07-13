package com.example.partx.services.user;

import com.example.partx.mappers.user.UserMapper;
import com.example.partx.models.dtos.user.RegisterDto;
import com.example.partx.models.dtos.user.UserDto;
import com.example.partx.models.entities.user.UserEntity;
import com.example.partx.models.enums.user.UserType;
import com.example.partx.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity registerUser(RegisterDto dto) {
        validateUser(dto);

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Email already in use: " + dto.getEmail());
        }

        UserEntity user = new UserEntity();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getUserType());
        user.setCountry(dto.getCountryOrigin());

        return userRepository.save(user);
    }

    public UserDto getById(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("User with id [%s] does not exist.".formatted(id))
                );

        return UserMapper.toUserDto(user);
    }

    public UserDto getByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new RuntimeException("Username with [%s] does not exist!".formatted(username))
                );

        return UserMapper.toUserDto(user);
    }

    public UserEntity findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    public void updateUserInfo(String currentUsername,
                               String newUsername, String newEmail, String newUserRole) {
        validateProfileInfo(newUsername, newEmail);

        UserEntity user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + currentUsername));

        Optional<UserEntity> existingEmailUser = userRepository.findByEmail(newEmail);
        if (existingEmailUser.isPresent() && !existingEmailUser.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Email address is already in use by another account.");
        }

        Optional<UserEntity> existingNameUser = userRepository.findByUsername(newUsername);
        if (existingNameUser.isPresent() && !existingNameUser.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setRole(UserType.valueOf(newUserRole));

        userRepository.save(user);
    }

    public void updateUserPassword(String currentUsername, String currentPassword,
                                   String newPassword, String confirmPassword) {
        UserEntity user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Your current password does not match our records.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password configuration confirmation does not match.");
        }

        validateRawPassword(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void addMoneyToWallet(String username, double amount) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        BigDecimal amountToAdd = BigDecimal.valueOf(amount);
        BigDecimal currentBalance = user.getUserBalance() != null ? user.getUserBalance() : BigDecimal.ZERO;

        BigDecimal updatedBalance = currentBalance.add(amountToAdd);

        user.setUserBalance(updatedBalance);

        userRepository.save(user);
    }

    public void saveProfilePicture(String username, MultipartFile file) throws IOException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.setProfilePicture(file.getBytes());

        userRepository.save(user);
    }

    public String getBalanceByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        return String.format("%.2f", user.getUserBalance());
    }


    public String getRoleByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        return user.getRole().name();
    }


    //------PRIVATE HELPERS---------
    private void validateUser(RegisterDto user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username must not be empty");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email format is invalid");
        }
        validateRawPassword(user.getPassword());
    }

    private void validateRawPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
    }

    private void validateProfileInfo(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email format is invalid");
        }
    }
}