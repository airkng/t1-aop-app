package t1.edu.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tuser")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@EqualsAndHashCode(of = {"id", "username"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username", unique = true)
    private String username;
    @Column(name = "password")
    private String password;

}
