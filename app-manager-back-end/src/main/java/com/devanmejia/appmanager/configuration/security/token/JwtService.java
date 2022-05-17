package com.devanmejia.appmanager.configuration.security.token;

import com.devanmejia.appmanager.entity.user.Authority;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Setter
public class JwtService implements AccessTokenService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expired}")
    private Long timeValidation;

    @Override
    public String createAccessToken(String email, Authority authority){
        var claims = Jwts.claims()
                .setSubject(email);
        claims.put("authority", authority.name());
        var currentDate = new Date();
        var validationTime = new Date(currentDate.getTime() + timeValidation);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .setExpiration(validationTime)
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    @Override
    public String getEmail(String accessToken){
        try {
            return Jwts.parser()
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
            var expirationTime = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return !expirationTime.before(new Date());
        } catch(JwtException e){
            return false;
        }
    }
}
