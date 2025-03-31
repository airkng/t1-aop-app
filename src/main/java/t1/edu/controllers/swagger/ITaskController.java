package t1.edu.controllers.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import t1.edu.dto.request.TaskRequestDto;
import t1.edu.dto.response.TaskResponseDto;
import t1.edu.exceptions.ExceptionMessage;
import t1.edu.model.Task;

import java.util.List;

@Tag(name = "Task", description = "Простое API для сущности Task")
public interface ITaskController {
    @Operation(description = "Получение конкретной задачи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "задача успешно найдена и отправлена",
                    headers = {@Header(name = "authenticated", description = "просто кастомный хэдер, чтобы не было скучно")},
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задача с заданными параметрами не была найдена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class))}
            )
    })
    ResponseEntity<TaskResponseDto> getTask(@Parameter(description = "Уникальный идентификатор задачи", required = true, example = "1") Long taskId);

    @Operation(description = "Получение списка всех задач. Если задачи не найдены возвращается пустой список")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "задачи успешно найдены и отправлены",
                    headers = {@Header(name = "authenticated", description = "просто кастомный хэдер, чтобы не было скучно")},
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class))}
            )
    })
    ResponseEntity<List<TaskResponseDto>> getAllTasks();

    @Operation(description = "Получение подробного списка всех задач с пользователем. Если задачи не найдены возвращается пустой список")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "задачи успешно найдены и отправлены",
                    headers = {@Header(name = "authenticated", description = "просто кастомный хэдер, чтобы не было скучно")},
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))}
            )
    })
    List<Task> getVerboseInfo();

    @Operation(description = "Создание задачи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "задача успешно найдена и отправлена",
                    headers = {@Header(name = "authenticated", description = "просто кастомный хэдер, чтобы не было скучно")},
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задача с заданными параметрами не была найдена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class))}
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Задача с заданными параметрами уже существует. Уникальной считается та запись, у которой нет" +
                            "повторяющихся значений по параметрам: userId, description, title",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class))}
            )
    })
    ResponseEntity<TaskResponseDto> createTask(
            @RequestBody(
                    description = "Тело запроса для создания задачи",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskRequestDto.class))}) TaskRequestDto requestDto
    );

    @Operation(description = "Обновление существующей задачи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "задача успешно найдена и отправлена",
                    headers = {@Header(name = "authenticated", description = "просто кастомный хэдер, чтобы не было скучно")},
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задача с заданным идентификатором не была найдена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class))}
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Задача с заданными параметрами уже существует. Уникальной считается та запись, у которой нет" +
                            "повторяющихся значений по параметрам: userId, description, title",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessage.class))}
            )
    })
    ResponseEntity<TaskResponseDto> updateTask(
            @Parameter(description = "уникальный идентификатор задачи", required = true, example = "1") Long taskId,
            @RequestBody(description = "обновленные данные") TaskRequestDto requestDto);

    @Operation(description = "Удаление задачи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "задача успешно удалена",
                    headers = {@Header(name = "authenticated", description = "просто кастомный хэдер, чтобы не было скучно")}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задача с заданными параметрами не была найдена, но удалена",
                    content = {@Content(mediaType = "void")}
            )
    })
    ResponseEntity<Void> deleteTask(
            @Parameter(description = "уникальный идентификатор задачи", required = true, example = "1") Long taskId);

}
