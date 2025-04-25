package t1.edu.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskResponseDtoTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final TaskResponseDto dto = TaskResponseDto.builder().id(1L).status("OLD").userId(2L).description("desc").title("title").build();

    @Test
    public void serializeDto_dtoFromJson_returnsDto() throws JsonProcessingException {
        String json = """
                {"id":1,"description":"desc","status":"OLD","title":"title","userId":2}
                """;
        TaskResponseDto result = mapper.readValue(json, TaskResponseDto.class);
        assertEquals(dto.getStatus(), result.getStatus());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getUserId(), result.getUserId());
    }

    @Test
    public void deserializeDto_jsonFromDto_returnsString() throws JsonProcessingException {
        String actual = """
                {"id":1,"description":"desc","status":"OLD","title":"title","userId":2}""";
        String result = mapper.writeValueAsString(dto);
        assertEquals(actual, result);
    }
}
