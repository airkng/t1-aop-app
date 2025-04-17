package t1.edu.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TaskKafkaDto {
    private String id;
    private String status;
}
