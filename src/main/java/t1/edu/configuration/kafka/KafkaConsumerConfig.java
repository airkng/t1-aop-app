package t1.edu.configuration.kafka;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import t1.edu.configuration.MessageJsonDeserializer;
import t1.edu.dto.TaskKafkaDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "t1.kafka.consumer")
@Setter
@Slf4j
public class KafkaConsumerConfig {
    private Map<String, Object> props;

    //бин для создания консьюмера апаче кафка пакета
    @Bean
    public ConsumerFactory<String, TaskKafkaDto> consumerListenerFactory() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //десериализатор ключа
        cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageJsonDeserializer.class); //десеарилизатор значения
        cfg.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.get("servers")); //сервер кафки
        cfg.put(ConsumerConfig.GROUP_ID_CONFIG, props.get("groupId")); //конфиг группы консьюмера
        cfg.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, props.get("sessionTimeoutMs")); //время после которого брокер считает, что консьюмер умер
        cfg.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, props.get("enableAutoCommit")); //автокоммит смещения при прочтении
        cfg.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, props.get("maxPollRecords")); //сколько можно прочитать сообщений за раз и коммит оффсета
        cfg.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        cfg.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "t1.edu.dto.TaskKafkaDto"); //во что маппится байты из кафки
        cfg.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); //каким пакетам можно доверять при десериализации
        cfg.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false); //Используются ли заголовки
        cfg.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageJsonDeserializer.class);
        cfg.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageJsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(cfg);
    }

    //насколько я понял бин для создания мультипоточной обработки событий кафки
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskKafkaDto> kafkaListenerContainer(
            @Qualifier("consumerListenerFactory") ConsumerFactory<String, TaskKafkaDto> consumerFactory
    ) {
        var listener = new ConcurrentKafkaListenerContainerFactory<String, TaskKafkaDto>();
        configureListener(listener, consumerFactory);
        return listener;
    }

    private <T> void configureListener(
            ConcurrentKafkaListenerContainerFactory<String, T> listener,
            ConsumerFactory<String, T> consumerFactory
    ) {
        listener.setConcurrency(1); //количество консьюмеров
        listener.setBatchListener(true);
        listener.setConsumerFactory(consumerFactory);
        listener.setCommonErrorHandler(errorHandler()); //обработчик ошибок
        listener.getContainerProperties().setMicrometerEnabled(true);
        listener.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        listener.getContainerProperties().setPollTimeout(5000);
    }

    //бин для создания обработчика исключений
    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(2000L, 3));
        errorHandler.setRetryListeners(((record, ex, deliveryAttempt) -> {
            log.error("Kafka listener message: {}, offset = {}, attempt = {}.", ex.getMessage(), record.offset(), deliveryAttempt);
        }));
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }
}
