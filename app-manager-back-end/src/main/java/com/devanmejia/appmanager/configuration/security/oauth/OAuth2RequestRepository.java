package com.devanmejia.appmanager.configuration.security.oauth;

import com.devanmejia.appmanager.configuration.security.oauth.cookie.CookieService;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@AllArgsConstructor
public class OAuth2RequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private final static String REDIRECTION_URI_PARAM_NAME = "redirect_uri";
    private final static String ERROR_URI_PARAM_NAME = "error_uri";

    private final CookieService cookieService;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return cookieService.getAuthRequest(request)
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            cookieService.deleteRedirectionURI(request, response);
            cookieService.deleteAuthRequest(request, response);
            return;
        }
        cookieService.setAuthRequest(response, authorizationRequest);

        var redirectionURI = request.getParameter(REDIRECTION_URI_PARAM_NAME);
        if (redirectionURI != null && !redirectionURI.isBlank()) {
            cookieService.setRedirectionURI(response, redirectionURI);
        }

        var errorURI = request.getParameter(ERROR_URI_PARAM_NAME);
        if (errorURI != null && !errorURI.isBlank()) {
            cookieService.setErrorURI(response, errorURI);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.loadAuthorizationRequest(request);
    }

}
