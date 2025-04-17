package t1.edu.configuration.kafka;

import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.requests.CreateTopicsRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("t1.kafka.topic")
@Setter
public class KafkaTopic {
    private String name;
    private Integer partitionCount;
    private String defaultName;


    @Bean
    @ConditionalOnProperty(value = "t1.kafka.topic.enable", havingValue = "true")
    public NewTopic taskTopic() {
        return new NewTopic(name, partitionCount, CreateTopicsRequest.NO_REPLICATION_FACTOR);
    }

    @Bean
    @ConditionalOnProperty(value = "t1.kafka.topic.enable", havingValue = "false")
    public NewTopic defaultTaskTopic() {
        return new NewTopic(defaultName, 1, CreateTopicsRequest.NO_REPLICATION_FACTOR);
    }
}
