package com.devanmejia.appmanager.configuration.security;

import com.devanmejia.appmanager.security.oauth.OAuth2RequestRepository;
import com.devanmejia.appmanager.entity.user.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${cors.allowed.origin.pattern}")
    private String allowedOriginPattern;
    private final OncePerRequestFilter jwtAuthenticationFilter;
    private final AuthenticationManager jwtAuthenticationManager;
    private final AuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final OAuth2RequestRepository authorizationRequestRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authenticationManager(jwtAuthenticationManager)
                .addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .exceptionHandling(exceptionHandlingConfig -> exceptionHandlingConfig
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeRequests(authorizeRequestsConfig -> authorizeRequestsConfig
                        .antMatchers("/api/v1/user/reset/*").hasAuthority(Authority.UPDATE_NOT_CONFIRMED.name())
                        .antMatchers("/api/v1/user").hasAuthority(Authority.UPDATE_CONFIRMED.name())
                        .antMatchers("/api/v1/auth/**", "/api/v1/user/reset", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
                        .antMatchers("/api/v1/apps/**", "/api/v1/app/**").hasAuthority(Authority.ACTIVE.name())
                        .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/api/v1/auth/oauth2")
                                .authorizationRequestRepository(authorizationRequestRepository))
                        .successHandler(successHandler)
                        .failureHandler(failureHandler));
    }

    @Bean
    public CorsFilter corsFilter() {
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern(allowedOriginPattern);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        var configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configSource);
    }

    @Bean
    public Filter corsHeaderFilter() {
        return (request, response, chain) -> {
            var httpResponse = (HttpServletResponse) response;
            httpResponse.addHeader("Access-Control-Expose-Headers", "X-Total-Count");
            chain.doFilter(request, response);
        };
    }

}
