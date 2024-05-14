package com.sau.bankingmanagementgradle.repositories;

import com.sau.bankingmanagementgradle.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    UserEntity findByUsername(String userName);
    UserEntity findByEmail(String email);
}
