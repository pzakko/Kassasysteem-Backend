package nl.fontys.kassasysteem.kassa_systeem_backend.repository;

import nl.fontys.kassasysteem.kassa_systeem_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
