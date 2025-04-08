package t1.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import t1.edu.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Modifying
    @Query(value = "DELETE FROM ttask where id = :taskId returning count(*)", nativeQuery = true)
    int deleteTaskById(Long taskId);

    boolean existsByDescriptionAndTitleAndUserId(String description, String title, Long userId);
}
