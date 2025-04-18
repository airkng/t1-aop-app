package t1.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import t1.edu.dto.request.TaskRequestDto;
import t1.edu.dto.response.TaskFullResponseDto;
import t1.edu.dto.response.TaskResponseDto;
import t1.edu.exceptions.AlreadyExistsException;
import t1.edu.exceptions.NotFoundException;
import t1.edu.kafka.KafkaTaskProducer;
import t1.edu.mappers.TaskMapper;
import t1.edu.model.Task;
import t1.edu.model.TaskStatus;
import t1.edu.model.User;
import t1.edu.repository.TaskRepository;
import t1.edu.repository.UserRepository;
import t1.edu.service.NotificationService;
import t1.edu.service.TaskService;
import t1.edu.utils.annotations.HandleResult;
import t1.edu.utils.annotations.Loggable;
import t1.edu.utils.annotations.AlertException;
import t1.edu.utils.annotations.TestPerformance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static t1.edu.utils.CommonMessages.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final KafkaTaskProducer kafkaTaskProducer;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper mapper;
    private Random rnd = new Random();

    /**
     * Получение задачи по уникальному идентификатору. В случае, если задачи не существует происходит выброс исключения
     * @param taskId уникальный идентификатор задачи
     * @return {@link TaskResponseDto TaskResponseDto}
     * @throws NotFoundException исключение, связанное с отсутствием сущности
     */
    @Override
    @Loggable
    @AlertException
    @TestPerformance
    public TaskResponseDto getTaskById(Long taskId) {
        return mapper.toResponseDto(
                taskRepository.findById(taskId)
                        .orElseThrow(() -> new NotFoundException(
                                String.format(TASK_NOT_FOUND_MESSAGE, taskId)
                        ))
        );
    }

    /**
     * Получение всех задач и конвертация в dto для респонса
     * @return {@link TaskResponseDto TaskResponseDto}
     */
    @Override
    @Loggable
    public List<TaskResponseDto> getAllTasks() {
        List<TaskResponseDto> list = new ArrayList<>();
        taskRepository.findAll().forEach(s -> {
            list.add(mapper.toResponseDto(s));
        });
        return list;
    }

    @Transactional(readOnly = true)
    @Override
    @Loggable
    public List<TaskFullResponseDto> getTasksVerbose() {
        return taskRepository.findAll()
                .stream()
                .peek((task) -> {
                    User u = userRepository.findById(task.getUser().getId()).get();
                    task.setUser(User.builder()
                            .username(u.getUsername())
                            .password(u.getPassword())
                            .id(u.getId())
                            .build()
                    );
                })
                .map(mapper::toFullResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Создание задачи. Если userId не существует - создается рандомный пользователь.
     * @param requestDto {@link TaskRequestDto TaskRequestDto}
     * @return возвращает {@link TaskResponseDto TaskResponseDto}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @AlertException
    @Loggable
    @TestPerformance
    @HandleResult
    @Override
    public TaskResponseDto createTask(TaskRequestDto requestDto) {
        //Создание рандомного пользователя
        var customUser = generateUser(requestDto.getUserId());
        User user = userRepository.findById(requestDto.getUserId())
                .orElseGet(() -> {
                    //добавление рандомного пользователя, если он не существует в БД
                    userRepository.saveCustomUser(customUser.getUsername(), customUser.getPassword(), customUser.getId());
                    return customUser;
                });
        if (taskRepository.existsByDescriptionAndTitleAndUserId(requestDto.getDescription(), requestDto.getTitle(), requestDto.getUserId())) {
            throw new AlreadyExistsException(String.format(TASK_ALREADY_EXISTS_MESSAGE, requestDto));
        }
        Task task = mapper.toModel(requestDto, user);
        return mapper.toResponseDto(taskRepository.save(task));
    }

    private User generateUser(Long userId) {
        String username = generateRandomText("Abcdefghijklmnop", 10);
        String password = generateRandomText("Abcdefghijklmnop", 6);
        return User.builder()
                .id(userId)
                .username(username)
                .password(password)
                .build();
    }

    private String generateRandomText(String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < text.length; i++) {
            text[i] = characters.charAt(rnd.nextInt(characters.length()));
        }
        return new String(text);
    }

    @Override
    @Loggable
    @AlertException
    @TestPerformance
    @HandleResult
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public TaskResponseDto updateTask(TaskRequestDto requestDto, Long taskId) {
        Task task = taskRepository.findById(taskId)
                        .orElseThrow(() -> new NotFoundException(
                                String.format(TASK_NOT_FOUND_MESSAGE, taskId)
                        ));
        if (taskRepository.existsByDescriptionAndTitleAndUserId(requestDto.getDescription(), requestDto.getTitle(), requestDto.getUserId())) {
            throw new AlreadyExistsException(String.format(TASK_ALREADY_EXISTS_MESSAGE, requestDto));

        }
        User user = userRepository.findById(requestDto.getUserId())
                        .orElseThrow(() -> new NotFoundException(
                                String.format(USER_NOT_FOUND_MESSAGE, requestDto.getUserId())
                        ));
        task.setDescription(requestDto.getDescription() == null ? task.getDescription() : requestDto.getDescription());
        task.setTitle(requestDto.getTitle() == null ? task.getTitle() : requestDto.getTitle());
        task.setUser(user);
        TaskStatus status = task.getStatus();
        if (requestDto.getStatus() != null && (!status.toString().equalsIgnoreCase(requestDto.getStatus()))
        ) {
            task.setStatus(TaskStatus.valueOf(requestDto.getStatus().toUpperCase()));
            Task saved = taskRepository.save(task);
            taskRepository.flush();
            kafkaTaskProducer.produceEvent(mapper.toKafkaDto(task));
            return mapper.toResponseDto(saved);
        }
        return mapper.toResponseDto(taskRepository.save(task));
    }

    @Loggable
    @Override
    public boolean deleteTask(Long taskId) {
        return taskRepository.deleteTaskById(taskId) == 1 ? true : false;
    }

}
