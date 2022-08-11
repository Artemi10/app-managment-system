package com.devanmejia.appmanager.security.oauth.cookie;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Service
public class OAuthCookieService implements CookieService {
    private final static String OAUTH2_REQUEST_KEY = "oauth2_auth_request";
    private final static String REDIRECTION_URI_KEY = "redirect_uri";
    private final static String ERROR_URI_KEY = "error_uri";
    private final static int COOKIE_EXPIRATION_TIME = 180;

    @Override
    public Optional<String> getRedirectionURI(HttpServletRequest request) {
        return CookieUtils.getCookie(request, REDIRECTION_URI_KEY)
                .map(Cookie::getValue);
    }

    @Override
    public void setRedirectionURI(HttpServletResponse response, String redirectionURI) {
        CookieUtils.addCookie(response, REDIRECTION_URI_KEY, redirectionURI, COOKIE_EXPIRATION_TIME);
    }

    @Override
    public void deleteRedirectionURI(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, REDIRECTION_URI_KEY);
    }

    @Override
    public Optional<OAuth2AuthorizationRequest> getAuthRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, OAUTH2_REQUEST_KEY)
                .map(cookie -> CookieUtils.deserialize(cookie.getValue(), OAuth2AuthorizationRequest.class));
    }

    @Override
    public void setAuthRequest(HttpServletResponse response, OAuth2AuthorizationRequest authorizationRequest) {
        var value = CookieUtils.serialize(authorizationRequest);
        CookieUtils.addCookie(response, OAUTH2_REQUEST_KEY, value, COOKIE_EXPIRATION_TIME);
    }

    @Override
    public void deleteAuthRequest(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_REQUEST_KEY);
    }

    @Override
    public Optional<String> getErrorURI(HttpServletRequest request) {
        return CookieUtils.getCookie(request, ERROR_URI_KEY)
                .map(Cookie::getValue);
    }

    @Override
    public void setErrorURI(HttpServletResponse response, String errorURI) {
        CookieUtils.addCookie(response, ERROR_URI_KEY, errorURI, COOKIE_EXPIRATION_TIME);
    }

    @Override
    public void deleteErrorURI(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, ERROR_URI_KEY);
    }

    @Override
    public void deleteAllCookies(HttpServletRequest request, HttpServletResponse response) {
        this.deleteAuthRequest(request, response);
        this.deleteErrorURI(request, response);
        this.deleteRedirectionURI(request, response);
    }

    private static class CookieUtils {

        public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
            var cookies = request.getCookies();
            if (cookies == null) {
                return Optional.empty();
            }
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(name))
                    .findFirst();
        }

        public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
            var cookie = new Cookie(name, value);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);
        }

        public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
            getCookie(request, name)
                    .ifPresent(cookie -> {
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    });
        }

        public static String serialize(Object object) {
            return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
        }

        public static <T> T deserialize(String value, Class<T> cls) {
            return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(value)));
        }
    }
}
