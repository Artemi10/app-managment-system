package com.devanmejia.appmanager.security.token;

import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.service.time.TimeService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.FixedClock;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Setter
@RequiredArgsConstructor
public class JwtService implements AccessTokenService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expired}")
    private Long validTimePeriod;
    private final TimeService timeService;

    @Override
    public String createAccessToken(String email, Authority authority){
        var claims = Jwts.claims()
                .setSubject(email);
        claims.put("authority", authority.name());
        var currentTime = timeService.now();
        var expirationTime = currentTime.plusSeconds(validTimePeriod);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(currentTime.toInstant()))
                .setExpiration(Date.from(expirationTime.toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    @Override
    public String getEmail(String accessToken){
        try {
            return Jwts.parser()
                    .setClock(new FixedClock(Date.from(timeService.now().toInstant())))
                    .setSigningKey(secretKey)
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException exception) {
            return exception.getClaims().getSubject();
        }
    }

    @Override
    public String getAuthorityName(String accessToken) {
        try {
            return Jwts.parser()
                    .setClock(new FixedClock(Date.from(timeService.now().toInstant())))
                    .setSigningKey(secretKey)
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .get("authority", String.class);
        } catch (ExpiredJwtException exception) {
            return exception.getClaims().get("authority", String.class);
        }
    }

    @Override
    public boolean isValid(String token){
        try {
            var currentTime = timeService.now().toInstant();
            var expirationTime = Jwts.parser()
                    .setClock(new FixedClock(Date.from(timeService.now().toInstant())))
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .toInstant();
            return !expirationTime.isBefore(currentTime);
        } catch(JwtException e){
            return false;
        }
    }

}
