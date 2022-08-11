package com.devanmejia.appmanager.security.oauth;

import com.devanmejia.appmanager.security.oauth.cookie.CookieService;
import com.devanmejia.appmanager.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${oauth.default.redirect.uri}")
    private String defaultRedirectionURI;

    private final CookieService cookieService;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        var email = (String) ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("email");
        var token = authService.logInWithOAuth(email);
        var redirectionURI = cookieService.getRedirectionURI(request)
                .orElse(defaultRedirectionURI);
        var targetURI = UriComponentsBuilder
                .fromUriString(redirectionURI)
                .queryParam("token", token)
                .build()
                .toUriString();
        cookieService.deleteAllCookies(request, response);
        response.sendRedirect(targetURI);
    }
}
