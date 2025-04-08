package t1.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import t1.edu.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query(value = "insert into tuser values(:id, :username, :password)", nativeQuery = true)
    void saveCustomUser(
            @Param(value = "username") String username,
            @Param(value = "password") String password,
            @Param(value = "id") Long id);
}
