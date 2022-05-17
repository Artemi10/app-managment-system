package com.devanmejia.appmanager.configuration;

import com.devanmejia.appmanager.configuration.security.details.UserPrincipal;
import com.devanmejia.appmanager.entity.user.Authority;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class TestUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        switch (username) {
            case "lyah.artem10@mail.ru" -> {
                var authorities = List
                        .of(new SimpleGrantedAuthority(Authority.ACTIVE.name()));
                return new UserPrincipal(
                        1,
                        "lyah.artem10@mail.ru",
                        "$2y$10$LHUDwkfwe1GsXZ7Z0qJKWO6JlDFjQRfrQMclOI9ceQBF4V2Eo7AF",
                        authorities);
            }
            case "lyah.artem10@gmail.com" -> {
                var authorities = List
                        .of(new SimpleGrantedAuthority(Authority.UPDATE_CONFIRMED.name()));
                return new UserPrincipal(
                        2,
                        "lyah.artem10@gmail.com",
                        "$2y$10$LHUDwkfwe1GsXZ7Z0qJKWO6JlDFjQRfrQMclOI9ceQBF4V2Eo7AF",
                        authorities);
            }
            case "lyah.artem11@gmail.com" -> {
                var authorities = List
                        .of(new SimpleGrantedAuthority(Authority.UPDATE_NOT_CONFIRMED.name()));
                return new UserPrincipal(
                        3,
                        "lyah.artem11@gmail.com",
                        "$2y$10$LHUDwkfwe1GsXZ7Z0qJKWO6JlDFjQRfrQMclOI9ceQBF4V2Eo7AF",
                        authorities);
            }
            default -> throw new BadCredentialsException("Credentials are invalid");
        }
    }
}
