package com.devanmejia.appmanager.security.details;

import com.devanmejia.appmanager.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


public record UserPrincipal(
        long id,
        String email,
        String password,
        Collection<? extends GrantedAuthority> authorities)
        implements UserDetails {

    public UserPrincipal(User user) {
       this(
               user.getId(),
               user.getEmail(),
               user.getPassword(),
               Collections.singletonList(new SimpleGrantedAuthority(user.getAuthority().name())));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
