package com.sau.bankingmanagementgradle.servicesForUser.impl;

import com.sau.bankingmanagementgradle.dtos.RegistrationDto;
import com.sau.bankingmanagementgradle.models.Role;
import com.sau.bankingmanagementgradle.models.UserEntity;
import com.sau.bankingmanagementgradle.repositories.RoleRepository;
import com.sau.bankingmanagementgradle.repositories.UserRepository;
import com.sau.bankingmanagementgradle.servicesForUser.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(RegistrationDto registrationDto) {
        UserEntity user = new UserEntity();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        Role role = roleRepository.findByName("user");
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public String getUserRole(String username) {
        // Kullanıcı adına göre kullanıcıyı bul
        UserEntity user = userRepository.findByUsername(username);

        if (user != null) {
            // Kullanıcının rollerini al
            List<Role> roles = user.getRoles();

            // Eğer kullanıcının rolleri null değilse ve en az bir rolü varsa
            if (roles != null && !roles.isEmpty()) {
                // İlk rolü döndür, burada varsayılan olarak ilk rol alınacak
                // Eğer kullanıcının birden fazla rolü varsa, rol sırasına göre düzenleme yapabilirsiniz
                return roles.get(0).getName();
            }
        }

        // Kullanıcı yoksa veya rolleri yoksa null döndür
        return null;
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}