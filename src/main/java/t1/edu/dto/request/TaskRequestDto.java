package t1.edu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TaskRequestDto {
    private String description;
    private String title;
    @NotNull
    private Long userId;
}
