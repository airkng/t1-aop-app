package t1.edu.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ttask",
uniqueConstraints = @UniqueConstraint(columnNames = {"description", "title", "user_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
