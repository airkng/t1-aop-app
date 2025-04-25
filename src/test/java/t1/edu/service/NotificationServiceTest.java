package t1.edu.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import t1.edu.service.impl.NotificationServiceImpl;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {JavaMailSender.class, NotificationServiceImpl.class})
public class NotificationServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JavaMailSender mailSender;
    @InjectMocks
    private NotificationServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendMessageException_allOk_returnsTrue() {
        Mockito.doNothing().when(mailSender).send(ArgumentMatchers.any(MimeMessage.class));
        service.sendExceptionMessage("test", new RuntimeException(), "test@test.com");

    }

    @Test
    public void sendMessage_allOk_returnsTrue() {
        Mockito.doNothing().when(mailSender).send(ArgumentMatchers.any(MimeMessage.class));
        service.sendExceptionMessage("test", new RuntimeException(), "test@test.com");

    }

    @Test
    public void sendMessage_throwsException_returnsFalse() {

    }

}
