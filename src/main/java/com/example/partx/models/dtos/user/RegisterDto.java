package com.example.partx.models.dtos.user;


import com.example.partx.models.enums.user.CountryOrigin;
import com.example.partx.models.enums.user.UserType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private UserType userType;
    private CountryOrigin countryOrigin;
}
