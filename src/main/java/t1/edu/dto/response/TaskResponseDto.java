package t1.edu.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "ответ задачи")
public class TaskResponseDto {
    private Long id;
    private String description;
    private String title;
    private Long userId;
}
