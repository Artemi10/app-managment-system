package com.devanmejia.appmanager.configuration.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.Security;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {

    static {
        var disabledAlgorithms =
                "RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC, anon, NULL";
        Security.setProperty("jdk.tls.disabledAlgorithms", disabledAlgorithms);
    }

    private final EmailCredentials credentials;
    private Properties emailPropertiesConfig;

    @PostConstruct
    public void init() throws IOException {
        emailPropertiesConfig = loadProperties("email-config.properties");
    }

    private Properties loadProperties(String path) throws IOException {
        var properties = new Properties();
        var inputStream = new ClassPathResource(path).getInputStream();
        properties.load(inputStream);
        return properties;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Message emailMessage(Session session) throws MessagingException {
        var message = new MimeMessage(session);
        message.setFrom(new InternetAddress(credentials.address()));
        return message;
    }

    @Bean
    protected Session session() {
        return Session.getDefaultInstance(emailPropertiesConfig, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(credentials.address(), credentials.password());
            }
        });
    }
}
