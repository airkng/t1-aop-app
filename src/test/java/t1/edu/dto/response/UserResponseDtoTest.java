package t1.edu.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserResponseDtoTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserResponseDto dto = UserResponseDto.builder()
            .username("username")
            .password("pass")
            .build();

    @Test
    public void serializeDto_returnsJsonString() throws JsonProcessingException {
        String actual = """
                {"username":"username","password":"pass"}""";
        String result = mapper.writeValueAsString(dto);
        assertEquals(actual, result);
    }

    @Test
    public void deserializeDto_objectMapper_returnsDto() throws JsonProcessingException {
        String json = """
                {"username":"username","password":"pass"}""";
        UserResponseDto result = mapper.readValue(json, UserResponseDto.class);
        assertEquals(dto.getPassword(), result.getPassword());
        assertEquals(dto.getUsername(), result.getUsername());
    }
}
