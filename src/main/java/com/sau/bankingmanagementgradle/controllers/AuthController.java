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
        UserEntity existingUserByEmail = userService.findByEmail(user.getEmail());
        if (existingUserByEmail != null) {
            bindingResult.rejectValue("email", "error.user", "There is already an account registered with this email.");
        }
        if (!user.isPasswordMatch()) {
            bindingResult.rejectValue("rePassword", "error.user", "Passwords don't match");
        }
        UserEntity existingUserByUsername = userService.findByUsername(user.getUsername());
        if (existingUserByUsername != null) {
            bindingResult.rejectValue("username", "error.user", "There is already a user registered with this username.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "auth/register";
        }
        userService.saveUser(user);
        return "redirect:/?success";
    }
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "auth/login";
    }


}