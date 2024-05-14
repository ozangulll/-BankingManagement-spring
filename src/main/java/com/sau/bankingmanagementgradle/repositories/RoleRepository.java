package com.sau.bankingmanagementgradle.repositories;

import com.sau.bankingmanagementgradle.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Role findByName(String name);
}
