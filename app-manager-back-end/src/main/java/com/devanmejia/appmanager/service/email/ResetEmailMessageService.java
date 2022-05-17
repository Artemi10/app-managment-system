package com.devanmejia.appmanager.service.email;


import com.devanmejia.appmanager.exception.EmailException;
import com.devanmejia.appmanager.service.email.sender.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;

@Service
public class ResetEmailMessageService implements MessageService {
    private MessageSender sender;

    @Autowired
    public ResetEmailMessageService(MessageSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String email, String content) {
        try{
            var message = emailMessage();
            setContent(message, email, content);
            sender.send(message);
        } catch (MessagingException exception) {
            throw new EmailException("Can not send email");
        }
    }

    private void setContent(Message message, String email, String content) throws MessagingException {
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject("Reset password");
        var text = String.format("Reset password code: %s", content);
        message.setText(text);
    }

    @Lookup
    protected Message emailMessage(){
        return null;
    }

    @Autowired
    public void setSender(MessageSender sender) {
        this.sender = sender;
    }
}
