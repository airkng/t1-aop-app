package t1.edu.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import t1.edu.dto.request.TaskRequestDto;
import t1.edu.dto.response.TaskResponseDto;
import t1.edu.exceptions.AlreadyExistsException;
import t1.edu.exceptions.NotFoundException;
import t1.edu.kafka.KafkaTaskProducer;
import t1.edu.model.Task;
import t1.edu.model.TaskStatus;
import t1.edu.model.User;
import t1.edu.repository.TaskRepository;
import t1.edu.repository.UserRepository;
import t1.edu.service.impl.TaskServiceImpl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest("spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration")
//,org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskServiceIntegrationDbTest {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    @MockitoBean
    private final KafkaTaskProducer producer;
    @InjectMocks
    private final TaskServiceImpl service;

    private TaskRequestDto req = TaskRequestDto.builder()
            .status("NEW")
            .userId(1l)
            .description("desc")
            .title("title")
            .build();
    private User user = User.builder()
            .username("username")
            .password("pass")
            .build();

    private Task model = Task.builder()
            .title("title")
            .status(TaskStatus.ACTUAL)
            .description("desc")
            .user(user)
            .build();

    @Test
    public void createTask_SaveToDb_returnsDto() {
        var us = userRepository.saveAndFlush(user);
        var result = service.createTask(req);
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertEquals("title", result.getTitle());
        Assertions.assertEquals("NEW", result.getStatus());
        Assertions.assertEquals(1, result.getId());
    }

    @Test
    public void createTask_alreadyExists_returnsDto() {
        var result = service.createTask(req);
        assertThrows(AlreadyExistsException.class, () -> {
            service.createTask(req);
        });
    }


    @Test
    public void updateTask_userNotExists_returnsException() {
        userRepository.saveAndFlush(user);
        taskRepository.save(model);
        TaskRequestDto dto = TaskRequestDto.builder()
                .description("test")
                .status("UPDATED")
                .title("test")
                .userId(-1L)
                .build();
        assertThrows(NotFoundException.class, () -> {
            service.updateTask(dto, 1L);
        });

    }

    @Test
    public void updateTask_TaskNotExists_returnsException() {
        assertThrows(NotFoundException.class, () -> {
            service.updateTask(req, -1L);
        });
    }

    @Test
    public void updateTask_taskExists_returnsUpdatedDto() {
        var user = userRepository.saveAndFlush(this.user);
        var task = taskRepository.saveAndFlush(this.model);
        doNothing().when(producer).produceEvent(ArgumentMatchers.any());
        task = taskRepository.findById(1L).get();
        TaskRequestDto update = TaskRequestDto.builder()
                .description("upd")
                .status("UPDATED")
                .title("upd")
                .userId(1L)
                .build();
        TaskResponseDto response = service.updateTask(update, 1L);
        assertEquals(update.getUserId(), response.getUserId());
        assertEquals(update.getStatus(), response.getStatus());
        assertEquals(update.getDescription(), response.getDescription());
        assertEquals(update.getTitle(), response.getTitle());

        assertEquals(1L, task.getId());
        assertEquals(model.getDescription(), task.getDescription());
        assertEquals(model.getStatus(), task.getStatus());
        assertEquals(model.getTitle(), task.getTitle());
    }

    @Test
    public void getTaskById_taskInDb_returnsDto() {
        userRepository.saveAndFlush(user);
        var actual = taskRepository.saveAndFlush(model);
        var result = service.getTaskById(1L);
        assertEquals(actual.getTitle(), result.getTitle());
        assertEquals(actual.getDescription(), result.getDescription());
        assertEquals(actual.getStatus().toString(), result.getStatus());
        assertEquals(actual.getUser().getId(), result.getUserId());

    }

    @Test
    public void getTaskById_taskNotExists_throwsException() {
        assertThrows(NotFoundException.class, () -> {
            service.getTaskById(-1L);
        });
    }

    @Test
    public void getAllTasks_emptyDb_returnsEmptyList() {
        var result = service.getAllTasks();
        assertEquals(true, result.isEmpty());
    }

    @Test
    public void getAllTasks_filled_returnsList() {
        userRepository.saveAndFlush(user);
        Task t2 = Task.builder()
                .status(TaskStatus.NEW)
                .description("d1")
                .title("t1")
                .user(user)
                .build();
        List<Task> actual = List.of(model, t2);
        actual = taskRepository.saveAll(actual);
        var result = service.getAllTasks();
        result = result.stream()
                .sorted(Comparator.comparing(TaskResponseDto::getId))
                .collect(Collectors.toList());
        assertEquals(actual.get(1).getId(), result.get(1).getId());
        assertEquals(actual.get(1).getTitle(), result.get(1).getTitle());
        assertEquals(actual.get(1).getDescription(), result.get(1).getDescription());
        assertEquals(actual.size(), result.size());
    }

    @Test
    public void updateTask_sameParamsWithOtherModel_returnsException() {
        userRepository.saveAndFlush(user);
        taskRepository.save(model);
        var model2 = Task.builder()
                .description("d11")
                .status(TaskStatus.NEW)
                .title("tt")
                .user(user)
                .build();
        taskRepository.save(model2);
        TaskRequestDto dto = TaskRequestDto.builder()
                .userId(user.getId())
                .title(model.getTitle())
                .status(model.getStatus().toString())
                .description(model.getDescription())
                .build();
        assertThrows(AlreadyExistsException.class, () -> {
            service.updateTask(dto, 2L);
        });
    }


}
