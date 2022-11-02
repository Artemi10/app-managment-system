package com.devanmejia.appmanager.service.token;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service("numericTokenGenerator")
public class NumericTokenGenerator implements TokenGenerator {

    @Override
    public String generatorToken(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

}
