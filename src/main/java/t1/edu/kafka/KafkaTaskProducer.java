package t1.edu.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import t1.edu.dto.TaskKafkaDto;
import t1.edu.utils.annotations.Loggable;

@RequiredArgsConstructor
public class KafkaTaskProducer {
    private final KafkaTemplate<String, TaskKafkaDto> template;
    private final NewTopic topic;

    @Loggable
    @Transactional
    public void produceEvent(final TaskKafkaDto dto) {
        if (topic != null) {
            template.send(topic.name(), dto.getId(), dto);
            template.flush();
        } else {
            template.sendDefault(dto.getId(), dto);
            template.flush();
        }
    }
}
