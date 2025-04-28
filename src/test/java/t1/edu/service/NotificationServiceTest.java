package t1.edu.service;

import jakarta.mail.internet.MimeMessage;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.internal.matchers.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import t1.edu.service.impl.NotificationServiceImpl;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {JavaMailSenderImpl.class, NotificationServiceImpl.class})
public class NotificationServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private NotificationServiceImpl service;

    @Test
    public void sendMessageException_allOk_returnsTrue() {
        Mockito.doNothing().when(mailSender).send(ArgumentMatchers.any(MimeMessage.class));
        service.sendExceptionMessage("test", new RuntimeException(), "test@test.com");

    }

    @Test
    public void sendMessage_allOk_returnsTrue() {
        Mockito.doNothing().when(mailSender).send(ArgumentMatchers.any(MimeMessage.class));
        var result = service.sendMessage("test", "test@test.com");
        var actual = true;
        assertEquals(actual, result);

    }

    @Test
    public void sendMessage_throwsException_returnsFalse() {
        Mockito.doThrow(new RuntimeException()).when(mailSender).send(ArgumentMatchers.isA(MimeMessage.class));
        var result = service.sendMessage("test", "test@test.com");
        var actual = true;
        assertEquals(actual, result);
    }

}
