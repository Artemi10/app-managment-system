package com.devanmejia.appmanager.configuration.security;

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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final OncePerRequestFilter jwtAuthenticationFilter;
    private final AuthenticationManager jwtAuthenticationManager;
    private final AuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Value("${cors.allowed.origin.pattern}")
    private String allowedOriginPattern;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .authenticationManager(jwtAuthenticationManager)
                .addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .exceptionHandling(exceptionHandlingConfig -> exceptionHandlingConfig
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeRequests(authorizeRequestsConfig -> authorizeRequestsConfig
                        .antMatchers("/api/v1/user/reset/*").hasAuthority(Authority.UPDATE_NOT_CONFIRMED.name())
                        .antMatchers("/api/v1/user").hasAuthority(Authority.UPDATE_CONFIRMED.name())
                        .antMatchers("/api/v1/auth/**", "/api/v1/user/reset").permitAll()
                        .antMatchers("/api/v1/apps/**", "/api/v1/app/**").hasAuthority(Authority.ACTIVE.name())
                        .anyRequest().authenticated());
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
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
