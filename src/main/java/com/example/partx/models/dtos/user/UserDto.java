package com.example.partx.models.dtos.user;

import com.example.partx.models.enums.user.CountryOrigin;
import com.example.partx.models.enums.user.UserType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String profilePicture;
    private String password;
    private BigDecimal userBalance;
    private UserType role;
    private CountryOrigin country;
}
