package t1.edu.service;

public interface NotificationService {
    void sendExceptionMessage(String message, Exception e, String mailTo);

    boolean sendMessage(String message, String mailTo);

}
