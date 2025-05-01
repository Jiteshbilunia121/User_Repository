package org.example.repository;


import org.example.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // For authentication purposes

    Optional<User> findById(Long id);

    boolean existsByEmail(String email); // To check if email is already registered
}
