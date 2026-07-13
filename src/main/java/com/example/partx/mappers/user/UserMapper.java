package com.example.partx.mappers.user;

import com.example.partx.models.dtos.user.UserDto;
import com.example.partx.models.entities.user.UserEntity;
import lombok.NoArgsConstructor;

import java.util.Base64;

@NoArgsConstructor
public class UserMapper {
    public static UserDto toUserDto(UserEntity user) {
        if (user == null) {
            return null;
        }

        String base64Image = null;
        if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) {
            base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getProfilePicture());
        }

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(base64Image)
                .userBalance(user.getUserBalance())
                .role(user.getRole())
                .country(user.getCountry())
                .build();
    }
}
