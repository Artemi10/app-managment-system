package com.devanmejia.appmanager.security.oauth;

import com.devanmejia.appmanager.security.oauth.cookie.CookieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Value("${oauth.default.error.uri}")
    private String defaultErrorURI;
    private final CookieService cookieService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        var errorURI = cookieService.getErrorURI(request)
                .orElse(defaultErrorURI);
        var targetURI = UriComponentsBuilder
                .fromUriString(errorURI)
                .queryParam("errorMessage", "Credentials are invalid")
                .build()
                .toUriString();
        cookieService.deleteAllCookies(request, response);
        response.sendRedirect(targetURI);
    }

}
