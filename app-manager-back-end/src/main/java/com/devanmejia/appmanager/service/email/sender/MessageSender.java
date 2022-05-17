package com.devanmejia.appmanager.service.email.sender;

import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;

@Service
public interface MessageSender {
    void send(Message message) throws MessagingException;
}
