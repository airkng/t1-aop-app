package t1.edu.configuration.kafka;

import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import t1.edu.dto.TaskKafkaDto;
import t1.edu.kafka.KafkaTaskProducer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "t1.kafka.producer")
@Setter
public class KafkaProducerConfig {
    private Map<String, Object> props;
    private NewTopic topic;

    @Bean
    public ProducerFactory<String, TaskKafkaDto> kafkaProducer() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.get("servers"));
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        cfg.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, props.get("enableIdempotence"));
        return new DefaultKafkaProducerFactory<>(cfg);
    }

    @Bean
    public KafkaTemplate<String, TaskKafkaDto> taskKafkaTemplate(ProducerFactory<String, TaskKafkaDto> producer) {
        return new KafkaTemplate<>(producer);
    }

    @Bean
    @ConditionalOnProperty(value = "t1.kafka.producer.props.enable", havingValue = "true", matchIfMissing = true)
    public KafkaTaskProducer clientProducer(
            @Qualifier("taskKafkaTemplate") KafkaTemplate<String, TaskKafkaDto> template,
            Environment env
    ) {
        template.setDefaultTopic("${t1.kafka.topic.name}");
        return new KafkaTaskProducer(template, env, topic);

    }


}
