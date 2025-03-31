package t1.edu.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import t1.edu.service.NotificationService;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    @Override
    public void sendExceptionMessage(String message, Exception e) {
      log.error("Message to admin: {}.\n Exception is thrown: {}", message, e);
        System.out.println("");
        System.out.println("======================");
        System.out.println("");
        System.out.println("SENDING NOTIFICATION");
        System.out.println("");
        System.out.println("======================");
    }
}
