package t1.edu.mappers;

import org.springframework.stereotype.Component;
import t1.edu.dto.request.TaskRequestDto;
import t1.edu.dto.response.TaskFullResponseDto;
import t1.edu.dto.response.TaskResponseDto;
import t1.edu.dto.response.UserResponseDto;
import t1.edu.model.Task;
import t1.edu.model.User;

@Component
public class TaskMapper {
    public TaskResponseDto toResponseDto(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .userId(task.getUser().getId())
                .description(task.getDescription())
                .title(task.getTitle())
                .build();
    }

    public Task toModel(TaskRequestDto dto, User user) {
        return Task.builder()
                .user(user)
                .description(dto.getDescription())
                .title(dto.getTitle())
                .build();
    }

    public TaskFullResponseDto toFullResponseDto(Task task) {
        return TaskFullResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .user(getUserResponseDto(task.getUser()))
                .build();
    }

    private UserResponseDto getUserResponseDto(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();

    }
}
