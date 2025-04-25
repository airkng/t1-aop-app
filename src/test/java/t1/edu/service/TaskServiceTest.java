package t1.edu.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import t1.edu.repository.TaskRepository;
import t1.edu.repository.UserRepository;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TaskService.class, UserRepository.class, TaskRepository.class})
public class TaskServiceTest {
}
