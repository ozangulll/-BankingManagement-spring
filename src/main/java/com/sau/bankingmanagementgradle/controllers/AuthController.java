package com.sau.bankingmanagementgradle.controllers;

import com.sau.bankingmanagementgradle.dtos.RegistrationDto;
import com.sau.bankingmanagementgradle.models.UserEntity;
import com.sau.bankingmanagementgradle.servicesForUser.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        RegistrationDto user = new RegistrationDto();
        model.addAttribute("user", user);
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegistrationDto user,
                               BindingResult bindingResult, Model model) {
        UserEntity existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            bindingResult.rejectValue("email", "There is already an account with this email");
        }
        UserEntity existingUser2 = userService.findByUsername(user.getUsername());
        if (existingUser2 != null && existingUser2.getUsername() != null && !existingUser2.getUsername().isEmpty()) {
            bindingResult.rejectValue("email", "There is already an user with this email");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
        }
        userService.saveUser(user);
        return "redirect:/?success";
    }
}