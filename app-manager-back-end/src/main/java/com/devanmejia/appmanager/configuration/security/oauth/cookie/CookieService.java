package com.devanmejia.appmanager.configuration.security.oauth.cookie;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
public interface CookieService {
    Optional<String> getRedirectionURI(HttpServletRequest request);
    Optional<String> getErrorURI(HttpServletRequest request);
    Optional<OAuth2AuthorizationRequest> getAuthRequest(HttpServletRequest request);
    void setRedirectionURI(HttpServletResponse response, String redirectionURI);
    void setErrorURI(HttpServletResponse response, String errorURI);
    void setAuthRequest(HttpServletResponse response, OAuth2AuthorizationRequest authorizationRequest);
    void deleteRedirectionURI(HttpServletRequest request, HttpServletResponse response);
    void deleteErrorURI(HttpServletRequest request, HttpServletResponse response);
    void deleteAuthRequest(HttpServletRequest request, HttpServletResponse response);
    void deleteAllCookies(HttpServletRequest request, HttpServletResponse response);
}
