package com.devanmejia.appmanager.service.email.sender;

import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

@Service
public class EmailMessageSender implements MessageSender {
    @Override
    public void send(Message message) throws MessagingException {
        Transport.send(message);
    }
}
