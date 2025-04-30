package t1.edu.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import t1.edu.dto.TaskKafkaDto;
import t1.edu.kafka.KafkaTaskConsumer;
import t1.edu.kafka.KafkaTaskProducer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class EmbeddedKafkaIntegrationTest {

    @Autowired
    private KafkaTaskConsumer consumer;

    @Autowired
    private KafkaTaskProducer producer;


    @Test
    public void givenEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived()
            throws Exception {
        TaskKafkaDto dto = TaskKafkaDto.builder()
                .status("NEW")
                .id("123")
                .build();
        String data = "Sending with our own simple KafkaProducer";

        producer.produceEvent(dto);

        boolean messageConsumed = consumer.isMessageConsumed();
        assertTrue(messageConsumed);
    }
}
