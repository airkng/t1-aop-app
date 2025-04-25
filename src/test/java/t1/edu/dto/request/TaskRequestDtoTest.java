package t1.edu.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import t1.edu.dto.request.TaskRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskRequestDtoTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private final TaskRequestDto dto = TaskRequestDto.builder()
            .title("title")
            .userId(1L)
            .status("OLD")
            .description("desc")
            .build();

    @Test
    public void jsonSerializationTest_convertToJson_returnsString() {
        try {
            String result = mapper.writeValueAsString(dto);
            String actual = """
                    {"description":"desc","title":"title","status":"OLD","userId":1}""";
            assertEquals(actual, result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void jsonDeserializationTest_convertToDto_returnsDto() {
        String jsonStr = """
                {"description":"desc","title":"title","status":"OLD","userId":1}""";
        try {
            TaskRequestDto taskRequestDto = mapper.readValue(jsonStr, TaskRequestDto.class);
            assertEquals(dto.getUserId(), taskRequestDto.getUserId());
            assertEquals(dto.getTitle(), taskRequestDto.getTitle());
            assertEquals(dto.getDescription(), taskRequestDto.getDescription());
            assertEquals(dto.getStatus(), taskRequestDto.getStatus());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
