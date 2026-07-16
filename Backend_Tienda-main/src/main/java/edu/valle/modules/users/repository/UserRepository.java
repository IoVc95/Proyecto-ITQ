package edu.valle.modules.users.repository;

import edu.valle.modules.users.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findFirstByActiveTrueOrderByIdAsc();

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
