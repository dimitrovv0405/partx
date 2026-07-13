package com.example.partx.models.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDto {
    @Email(message = "Email format is invalid")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
