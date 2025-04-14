package t1.edu.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import t1.edu.dto.TaskKafkaDto;
import t1.edu.service.NotificationService;
import t1.edu.utils.annotations.Loggable;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaTaskConsumer {
    private final NotificationService notificationService;

    @KafkaListener(id = "${t1.kafka.consumer.props.groupId}",
            topics = "${t1.kafka.topic.name}",
            containerFactory = "kafkaListenerContainer"
    )
    @Transactional
    @Loggable
    public void listener(@Payload List<TaskKafkaDto> dtos,
                         Acknowledgment acks,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        String message = "Обновление статуса задачи. Id = %s, status = %s";
        try {
            for (TaskKafkaDto dto: dtos) {
                String format = String.format(message, dto.getId(), dto.getStatus());
                notificationService.sendMessage(format, "delcher.dev@gmail.com");
            }
        } finally {
            acks.acknowledge();
        }
    }
}
