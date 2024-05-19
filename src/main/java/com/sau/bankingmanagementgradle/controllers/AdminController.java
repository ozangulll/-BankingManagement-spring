package com.sau.bankingmanagementgradle.controllers;

import com.sau.bankingmanagementgradle.models.Role;
import com.sau.bankingmanagementgradle.models.UserEntity;
import com.sau.bankingmanagementgradle.repositories.RoleRepository;
import com.sau.bankingmanagementgradle.repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@PreAuthorize("hasRole('ROLE_admin')")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/admin/users")
    public String getUsers(Model model) {
        List<UserEntity> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "/admin/user-list";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable int id) {
        userRepository.deleteUserRolesByUserId(id);
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/update/{id}")
    public String updateUser(@PathVariable int id,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam(required = false) boolean isAdmin) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            UserEntity existingUser = optionalUser.get();
            existingUser.setUsername(username);
            existingUser.setEmail(email);

            Role adminRole = roleRepository.findByName("admin");
            if (adminRole != null) {
                if (isAdmin && !existingUser.getRoles().contains(adminRole)) {
                    existingUser.getRoles().add(adminRole);
                } else if (!isAdmin) {
                    existingUser.getRoles().remove(adminRole);
                }
            }

            userRepository.save(existingUser);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable int id, Model model) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            return "/admin/user-edit";
        } else {
            return "redirect:/admin/users";
        }
    }
}
