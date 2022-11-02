package com.devanmejia.appmanager.service.token;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service("secureTokenGenerator")
public class SecureTokenGenerator implements TokenGenerator {
    private final SecureRandom secureRandom;
    private final Base64.Encoder encoder;

    public SecureTokenGenerator() {
        this.secureRandom = new SecureRandom();
        this.encoder = Base64.getUrlEncoder();
    }

    @Override
    public String generatorToken(int length) {
        var byteArray = new byte[length / 4 * 3];
        secureRandom.nextBytes(byteArray);
        return encoder.encodeToString(byteArray);
    }

}
