package t1.edu.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TaskFullResponseDto {
    private Long id;
    private String description;
    private String title;
    private UserResponseDto user;
}
