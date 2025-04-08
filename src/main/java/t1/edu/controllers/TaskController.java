package t1.edu.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import t1.edu.controllers.swagger.ITaskController;
import t1.edu.dto.request.TaskRequestDto;
import t1.edu.dto.response.TaskFullResponseDto;
import t1.edu.dto.response.TaskResponseDto;
import t1.edu.model.Task;
import t1.edu.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController implements ITaskController {
    private final TaskService service;
    //Просто так. Давно не использовал кастомные хэдеры при ответе, решил вспомнить ^_^
    private final String AUTHENTICATED_HEADER = "Authenticated";

    /**
     * Эндпоинт для получения задачи. В случае неправильного айди отправляет ошибку 404
     *
     * @param taskId уникальный идентификатор задачи
     * @return возвращает dto
     * {@link TaskResponseDto TaskResponseDto}
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable(name = "id") Long taskId) {
        return ResponseEntity.ok()
                .header(AUTHENTICATED_HEADER, String.valueOf(false))
                .body(service.getTaskById(taskId));
    }

    /**
     * Эндпоинт получения списка задач.
     *
     * @return возвращает список {@link List List} dto
     * {@link TaskResponseDto TaskResponseDto}
     */
    @Override
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        return ResponseEntity.ok()
                .header(AUTHENTICATED_HEADER, String.valueOf(false))
                .body(service.getAllTasks());
    }

    /**
     * Получение расширенной информации по таску - добавление информации о владельце задачи
     *
     * @return возвращает список сущности {@link Task Task}
     */
    @Override
    @GetMapping("/verbose")
    public List<TaskFullResponseDto> getVerboseInfo() {
        return service.getTasksVerbose();
    }

    /**
     * Эндпоинт создания задачи. В случае, если задача уже существует, отправляется ошибка 409
     *
     * @param requestDto объект {@link TaskRequestDto TaskRequestDto}
     * @return возвращает объект {@link TaskResponseDto TaskResponseDto}
     */
    @Override
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody TaskRequestDto requestDto) {
        return ResponseEntity.ok()
                .header(AUTHENTICATED_HEADER, String.valueOf(false))
                .body(service.createTask(requestDto));
    }

    /**
     * Обновление задачи. В случае, если задача изменяется на уже существующую, возвращается 409 ответ
     *
     * @param taskId     уникальный идентификатор задачи для изменения
     * @param requestDto измененная сущность {@link TaskRequestDto TaskRequestDto}
     * @return возвращает объект dto {@link TaskResponseDto TaskResponseDto}
     */
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable(name = "id") Long taskId,
            @RequestBody TaskRequestDto requestDto) {
        return ResponseEntity.ok()
                .header(AUTHENTICATED_HEADER, String.valueOf(false))
                .body(service.updateTask(requestDto, taskId));
    }

    /**
     * Удаление задачи. Возвращает 204 ответ в случае, если объект был удален, 404 ответ если не был найден
     *
     * @param taskId уникальный идентификатор задачи
     * @return void
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable(name = "id") Long taskId) {
        boolean isDeleted = service.deleteTask(taskId);
        if (isDeleted) {
            return ResponseEntity.noContent()
                    .header(AUTHENTICATED_HEADER, String.valueOf(false))
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(AUTHENTICATED_HEADER, String.valueOf(false))
                    .build();
        }
    }
}
