package t1.edu.mappers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import t1.edu.dto.TaskKafkaDto;
import t1.edu.dto.request.TaskRequestDto;
import t1.edu.dto.response.TaskFullResponseDto;
import t1.edu.dto.response.TaskResponseDto;
import t1.edu.model.Task;
import t1.edu.model.TaskStatus;
import t1.edu.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TaskMapper.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TaskMapperTest {
    private final TaskMapper mapper;

    private static User user;
    private static Task model;
    private static TaskRequestDto requestDto;

    @BeforeAll
    public static void init() {
        user = generateUser();
        model = generateModel();
        requestDto = generateRequestDto();
    }

    @Test
    public void mappingToResponse_whenModelExists_returnsResponseDto() {
        TaskResponseDto responseDto = mapper.toResponseDto(model);
        assertEquals(1L, responseDto.getId());
        assertEquals(2L, responseDto.getUserId());
        assertEquals("test", responseDto.getDescription());
        assertEquals("title", responseDto.getTitle());
    }

    @Test
    public void mappingToModel_whenDtoExists_returnsModel() {
        Task model = mapper.toModel(requestDto, user);
        assertEquals(user, model.getUser());
        assertEquals("desc", model.getDescription());
        assertEquals("title", model.getTitle());
        assertEquals(TaskStatus.OLD, model.getStatus());
    }

    @Test
    public void mappingToFullRespDto_whenEntitiesExists_returnFullDto() {
        TaskFullResponseDto dto = mapper.toFullResponseDto(model);
        assertEquals(1L, dto.getId());
        assertEquals("test", dto.getDescription());
        assertEquals("title", dto.getTitle());
        assertEquals(user.getUsername(), dto.getUser().getUsername());
        assertEquals(user.getPassword(), dto.getUser().getPassword());
        assertEquals(TaskStatus.ACTUAL.toString(), dto.getStatus());
    }

    @Test
    public void mappingToTaskKafkaDto_whenEntityExists_returnsDto() {
        TaskKafkaDto dto = mapper.toKafkaDto(model);
        assertEquals("1", dto.getId());
        assertEquals(TaskStatus.ACTUAL.toString(), dto.getStatus());
    }

    private static User generateUser() {
        return User.builder()
                .id(2L)
                .password("test")
                .username("username")
                .build();
    }

    private static Task generateModel() {
        return Task.builder()
                .id(1L)
                .description("test")
                .title("title")
                .status(TaskStatus.ACTUAL)
                .user(generateUser())
                .build();
    }

    private static TaskRequestDto generateRequestDto() {
        return TaskRequestDto.builder()
                .userId(1L)
                .description("desc")
                .status("OLD")
                .title("title")
                .build();
    }
}
