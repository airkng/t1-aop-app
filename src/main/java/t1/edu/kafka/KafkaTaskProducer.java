package t1.edu.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import t1.edu.dto.TaskKafkaDto;
import t1.edu.utils.annotations.Loggable;

@RequiredArgsConstructor
public class KafkaTaskProducer {
    private final KafkaTemplate<String, TaskKafkaDto> template;
    private final Environment env;
    private final NewTopic topic;

    @Loggable
    @Transactional
    public void produceEvent(final TaskKafkaDto dto) {
        if (topic != null) {
            template.send(topic.name(), dto.getId(), dto);
        } else {
            String defaultTopic = env.getProperty("t1.kafka.topic.name");
            template.send(defaultTopic, dto.getId(), dto);
        }
    }
}
