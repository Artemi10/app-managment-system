package com.devanmejia.appmanager.security.token;

import com.devanmejia.appmanager.entity.user.Authority;
import org.springframework.stereotype.Service;

@Service
public interface AccessTokenService {
    String createAccessToken(String email, Authority authority);
    String getEmail(String accessToken);
    String getAuthorityName(String accessToken);
    boolean isValid(String token);
}
