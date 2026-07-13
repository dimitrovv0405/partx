package com.example.partx.services;


import com.example.partx.models.entities.user.PartxUserDetails;
import com.example.partx.models.entities.user.UserEntity;
import com.example.partx.models.enums.user.UserType;
import com.example.partx.repositories.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Stream;


@AllArgsConstructor
public class PartxUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.userRepository
                .findByEmail(email)
                .map(PartxUserDetailService::map)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with email " +
                                email + " not found!"));
    }

    private static UserDetails map(UserEntity user) {
        return new PartxUserDetails(
                user.getUsername(),
                user.getPassword(),
                Stream.of(user.getRole()).map(userRole -> map(userRole)).toList(),
                user.getEmail()
        );
    }

    private static GrantedAuthority map(UserType role) {
        return new SimpleGrantedAuthority(role.name());
    }
}
