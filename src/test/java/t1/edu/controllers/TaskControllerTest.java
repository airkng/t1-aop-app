package t1.edu.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import t1.edu.dto.request.TaskRequestDto;
import t1.edu.dto.response.TaskResponseDto;
import t1.edu.exceptions.AlreadyExistsException;
import t1.edu.exceptions.NotFoundException;
import t1.edu.service.TaskService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    @SuppressWarnings("")
    private TaskService taskService;
    @InjectMocks
    private TaskController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private TaskResponseDto responseDto;
    private static final String CUSTOM_HEADER = "Authenticated";

    {
        responseDto = TaskResponseDto.builder()
                .id(1L)
                .title("example1")
                .title("title1")
                .userId(2L)
                .build();
        requestDto = TaskRequestDto.builder()
                .description("example2")
                .title("title2")
                .userId(3L)
                .build();
    }
    private TaskRequestDto requestDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        //  mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getTaskById_whenTaskExists_returnsOk() throws Exception {
        Mockito.when(taskService.getTaskById(1L)).thenReturn(responseDto);
        mockMvc.perform(srv -> MockMvcRequestBuilders.get("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .buildRequest(srv))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.header().exists(CUSTOM_HEADER))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(responseDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(responseDto.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(responseDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(responseDto.getTitle()));
    }

    @Test
    public void getTaskById_whenTaskNotExists_returnNotFound() throws Exception {
        NotFoundException ex = new NotFoundException("Задача не была найдена");
        Mockito.when(taskService.getTaskById(-1L)).thenThrow(ex);
        mockMvc.perform(get("/tasks/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ex.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"));
    }

    @Test
    public void getAllTasks_listTasks_returnsOk() throws Exception {
        List<TaskResponseDto> list = List.of(
                responseDto,
                TaskResponseDto.builder()
                        .title("title3")
                        .description("example3")
                        .userId(3L)
                        .id(2L)
                        .build()
        );
        Mockito.when(taskService.getAllTasks())
                .thenReturn(list);
        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.header().exists(CUSTOM_HEADER))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(responseDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").value(responseDto.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(responseDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value(responseDto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].userId").value(3L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("example3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value("title3"));
    }

    @Test
    public void createTask_taskNotExists_returnsOk() throws JsonProcessingException, Exception {
        Mockito.when(taskService.createTask(ArgumentMatchers.any(TaskRequestDto.class)))
                .thenReturn(responseDto);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))                        )
                .andExpect(MockMvcResultMatchers.header().exists(CUSTOM_HEADER))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(responseDto.getDescription()))
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.title").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.userId").value(responseDto.getUserId()));
    }

    @Test
    public void createTask_taskAlreadyExists_returnsDuplicate() throws Exception {
        AlreadyExistsException ex = new AlreadyExistsException("Таск уже существует");
        Mockito.when(taskService.createTask(ArgumentMatchers.any(TaskRequestDto.class)))
                .thenThrow(ex);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))                        )
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("409"))
                .andExpect(jsonPath("$.message").value(ex.getMessage()));
    }

    @Test
    public void updateTask_taskWithNewParams_returnsOk() throws Exception{
        Mockito.when(taskService.updateTask(ArgumentMatchers.any(TaskRequestDto.class), anyLong()))
                .thenReturn(responseDto);
        mockMvc.perform(put("/tasks/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(MockMvcResultMatchers.header().exists(CUSTOM_HEADER))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(responseDto.getDescription()))
                .andExpect(jsonPath("$.title").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.userId").value(responseDto.getUserId()));
    }

    @Test
    public void updateTask_duplicateTask_returnsConflict() throws Exception {
        Exception ex = new AlreadyExistsException("таск уже существует");
        Mockito.when(taskService.updateTask(ArgumentMatchers.any(TaskRequestDto.class), anyLong()))
                .thenThrow(ex);
        mockMvc.perform(put("/tasks/2")
                .content(objectMapper.writeValueAsBytes(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("409"))
                .andExpect(jsonPath("$.message").value(ex.getMessage()));

    }

    @Test
    public void deleteTask_correctId_returnsOk() throws Exception {
        Mockito.when(taskService.deleteTask(anyLong()))
                .thenReturn(true);
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent())
                .andExpect(header().exists(CUSTOM_HEADER));
    }

    @Test
    public void deleteTask_notExistId_returnsNotFound() throws Exception {
        Mockito.when(taskService.deleteTask(anyLong()))
                .thenReturn(false);
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(CUSTOM_HEADER));
    }


}
