package com.sau.bankingmanagementgradle.repositories;

import com.sau.bankingmanagementgradle.models.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    UserEntity findByUsername(String userName);
    UserEntity findByEmail(String email);
    UserEntity findFirstByUsername(String username);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM users_roles WHERE user_id = :userId", nativeQuery = true)
    void deleteUserRolesByUserId(@Param("userId") int userId);
}
