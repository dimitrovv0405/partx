package com.example.partx.models.entities.user;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class PartxUserDetails extends User {
    private final String email;

    public PartxUserDetails(String username, @Nullable String password,
                            Collection<? extends GrantedAuthority> authorities, String email) {
        super(username, password, authorities);
        this.email = email;
    }

    public PartxUserDetails(String username, @Nullable String password, boolean enabled,
                            boolean accountNonExpired, boolean credentialsNonExpired,
                            boolean accountNonLocked,
                            Collection<? extends GrantedAuthority> authorities, String email) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.email = email;
    }
}
