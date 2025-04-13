package t1.edu.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageJsonDeserializer<T> extends JsonDeserializer<T> {
    private String getErrorMessage(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(String topic, Headers headers, ByteBuffer data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            log.error("Ошибка во время десериализации топика '{}' в kafka: {}", topic, getErrorMessage(data.array()), e);
            return null;
        }
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            log.error("Ошибка во время десериализации топика '{}' в kafka: {}", topic, getErrorMessage(data), e);
            return null;
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            log.error("Ошибка во время десериализации топика '{}' в kafka: {}", topic, getErrorMessage(data), e);
            return null;
        }
    }
}
