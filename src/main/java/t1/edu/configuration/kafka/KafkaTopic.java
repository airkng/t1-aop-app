package t1.edu.configuration.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.requests.CreateTopicsRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class KafkaTopic {
    private final Environment env;

    @Bean
    @ConditionalOnProperty(value = "t1.kafka.topic.enable", havingValue = "true")
    public NewTopic taskTopic() {
        String topic = env.getProperty("t1.kafka.topic.name");
        Integer partitionCount = Integer.valueOf(env.getProperty("t1.kafka.topic.partition-count"));
        return new NewTopic(topic, partitionCount, CreateTopicsRequest.NO_REPLICATION_FACTOR);
    }
}
