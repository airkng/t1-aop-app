package t1.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import t1.edu.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ttask where id = :taskId returning count(*)", nativeQuery = true)
    int deleteTaskById(@Param(value = "taskId") Long taskId);

    boolean existsByDescriptionAndTitleAndUserId(String description, String title, Long userId);
}
