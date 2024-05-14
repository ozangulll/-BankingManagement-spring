package com.sau.bankingmanagementgradle.servicesForUser.impl;

import com.sau.bankingmanagementgradle.dtos.RegistrationDto;
import com.sau.bankingmanagementgradle.models.Role;
import com.sau.bankingmanagementgradle.models.UserEntity;
import com.sau.bankingmanagementgradle.repositories.RoleRepository;
import com.sau.bankingmanagementgradle.repositories.UserRepository;
import com.sau.bankingmanagementgradle.servicesForUser.UserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    public UserServiceImpl(UserRepository userRepository,RoleRepository roleRepository){
        this.roleRepository=roleRepository;
        this.userRepository=userRepository;
    }
    @Override
    public void saveUser(RegistrationDto registrationDto) {
        UserEntity user=new UserEntity();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(registrationDto.getPassword());
        Role role=roleRepository.findByName("user");
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return  userRepository.findByEmail(email);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
