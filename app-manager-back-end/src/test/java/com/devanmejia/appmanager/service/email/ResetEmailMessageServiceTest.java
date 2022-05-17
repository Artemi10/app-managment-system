package com.devanmejia.appmanager.service.email;

import com.devanmejia.appmanager.exception.EmailException;
import com.devanmejia.appmanager.service.email.sender.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ResetEmailMessageServiceTest {
    private final MessageSender sender;
    private final ResetEmailMessageService messageService;

    @Autowired
    public ResetEmailMessageServiceTest(ResetEmailMessageService resetEmailMessageService) {
        this.sender = spy(MessageSender.class);
        this.messageService = resetEmailMessageService;
        this.messageService.setSender(sender);
    }

    @BeforeEach
    public void initMock() throws Exception {
        doNothing()
                .when(sender)
                .send(argThat(message -> {
                    String address;
                    try {
                        address = message.getAllRecipients()[0].toString();
                    } catch (MessagingException exception) {
                        address = null;
                    }
                    return "lyah.artem10@mail.ru".equals(address);
                }));
        doThrow(new MessagingException())
                .when(sender)
                .send(argThat(message -> {
                    String address;
                    try {
                        address = message.getAllRecipients()[0].toString();
                    } catch (MessagingException exception) {
                        address = null;
                    }
                    return !"lyah.artem10@mail.ru".equals(address);
                }));
    }

    @Test
    public void sendMessage_Successfully_Test() throws MessagingException {
        var email = "lyah.artem10@mail.ru";
        var resetToken = "DVhclnWo";
        assertDoesNotThrow(() -> messageService.sendMessage(email, resetToken));
        verify(sender, times(1))
                .send(argThat(message -> {
                    String actualAddress, actualContent;
                    try {
                        actualAddress = message.getAllRecipients()[0].toString();
                        actualContent = message.getContent().toString();
                    } catch (Exception exception) {
                        actualAddress = null;
                        actualContent = null;
                    }
                    var messageContent = format("Reset password code: %s", resetToken);
                    return email.equals(actualAddress) && messageContent.equals(actualContent);
                }));
    }

    @Test
    public void throw_MessagingException_When_Can_Not_SendMessage_Test(){
        var email = "lyah.artem10@gmail.ru";
        var content = "Test message";
        var message = assertThrows(EmailException.class, () -> messageService.sendMessage(email, content));
        assertEquals("Can not send email", message.getMessage());
    }
}
