package com.sau.bankingmanagementgradle.servicesForUser;

import com.sau.bankingmanagementgradle.dtos.RegistrationDto;
import com.sau.bankingmanagementgradle.models.UserEntity;

public interface UserService {
    void saveUser(RegistrationDto registrationDto);
    String getUserRole(String username);
    UserEntity findByEmail(String email);

    UserEntity findByUsername(String username);
}
