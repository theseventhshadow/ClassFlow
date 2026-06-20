package com.ohiggins.classflow.auth.repository;

import com.ohiggins.classflow.auth.entity.Role;
import com.ohiggins.classflow.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdNumber(String idNumber);
    boolean existsByEmail(String email);
    boolean existsByIdNumber(String idNumber);
    
    List<User> findByRole(Role role);
    List<User> findByCourse(String course);
    List<User> findByGuardianId(Long guardianId);
}
