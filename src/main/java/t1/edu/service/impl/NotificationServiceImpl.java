package t1.edu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import t1.edu.service.NotificationService;

@Service
@Slf4j
@RequiredArgsConstructor
@Setter
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String email;
    @Override
    public void sendExceptionMessage(String message, Exception e, String mailTo) {
        log.warn("Message to admin: {}.", message);
        var textMessage = generateExceptionTextMessage(message, e);
        var mailMessage = createMailMessage(mailTo, email, textMessage);
        mailSender.send(mailMessage);
    }

    private String generateExceptionTextMessage(String message, Exception e) {
        StringBuilder builder = new StringBuilder();
        builder.append(message + "\n");
        builder.append("\n");
        builder.append("======================\n");
        builder.append("SENDING NOTIFICATION TO ADMIN \n");
        builder.append("\n");
        builder.append("Exception was thrown: ");
        builder.append(e.getLocalizedMessage() + "\n");
        builder.append("======================\n");
        return builder.toString();
    }

    private SimpleMailMessage createMailMessage(String mailTo, String mailFrom, String message) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setText(message);
        mailMessage.setTo(mailTo);
        mailMessage.setFrom(mailFrom);
        return mailMessage;
    }

    @Override
    public boolean sendMessage(String message, String mailTo) {
        var mailMessage = createMailMessage(mailTo, email, message);
        try {
            mailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            log.error("Ошибка во время отправки емаил сообщения", e);
            return false;
        }
    }
}
