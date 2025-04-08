package t1.edu.service;

import t1.edu.dto.request.TaskRequestDto;
import t1.edu.dto.response.TaskFullResponseDto;
import t1.edu.dto.response.TaskResponseDto;
import t1.edu.model.Task;

import java.util.List;

public interface TaskService {
    TaskResponseDto getTaskById(Long taskId);

    List<TaskResponseDto> getAllTasks();

    TaskResponseDto createTask(TaskRequestDto requestDto);

    TaskResponseDto updateTask(TaskRequestDto requestDto, Long taskId);

    boolean deleteTask(Long taskId);

    List<TaskFullResponseDto> getTasksVerbose();

}
