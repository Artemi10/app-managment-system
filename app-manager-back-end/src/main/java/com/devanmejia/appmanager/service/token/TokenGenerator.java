package com.devanmejia.appmanager.service.token;

import org.springframework.stereotype.Service;

@Service
public interface TokenGenerator {

    String generatorToken(int length);

}
