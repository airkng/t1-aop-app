package t1.edu.dto.response;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static t1.edu.model.TaskStatus.ACTUAL;

public class TaskFullResponseDtoTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private final TaskFullResponseDto dto = TaskFullResponseDto.builder()
            .status(ACTUAL.toString())
            .id(1L)
            .title("title")
            .description("desc")
            .user(new UserResponseDto("username", "pass"))
            .build();

    @Test
    public void serializeDto_byObjectMapper_returnsJson() throws JsonProcessingException {
        String result = mapper.writeValueAsString(dto);
        String actual = """
                {"id":1,"description":"desc","status":"ACTUAL","title":"title","user":{"username":"username","password":"pass"}}                """;
        assertEquals(actual, result);
    }

    @Test
    public void deserializeDto_byObjectMapper_returnsJson() throws JsonProcessingException {
        String jsonStr = """
                {"id":1,"description":"desc","status":"ACTUAL","title":"title","user":{"username":"username","password":"pass"}}                """;
        TaskFullResponseDto result = mapper.readValue(jsonStr, TaskFullResponseDto.class);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getStatus(), result.getStatus());
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(dto.getUser().getPassword(), result.getUser().getPassword());
    }
}
