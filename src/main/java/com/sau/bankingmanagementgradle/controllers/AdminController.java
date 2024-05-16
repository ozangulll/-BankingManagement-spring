package com.sau.bankingmanagementgradle.controllers;

import com.sau.bankingmanagementgradle.models.UserEntity;
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

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return "redirect:/admin/users";  // Kullanıcı silindikten sonra kullanıcı listesini yenilemek için yönlendirme
    }

    @PostMapping("/admin/users/update/{id}")
    public String updateUser(@PathVariable int id, @ModelAttribute UserEntity updatedUser, Model model) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            UserEntity existingUser = optionalUser.get();
            existingUser.setUsername(updatedUser.getUsername());  // Örnek olarak sadece kullanıcı adını güncelliyoruz
            existingUser.setEmail(updatedUser.getEmail());
            // Diğer alanları da güncellemek isterseniz buraya ekleyebilirsiniz
            userRepository.save(existingUser);
        }
        return "redirect:/admin/users";  // Kullanıcı güncellendikten sonra kullanıcı listesini yenilemek için yönlendirme
    }

    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable int id, Model model) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            return "/admin/user-edit";
        } else {
            return "redirect:/admin/users";  // Kullanıcı bulunamazsa kullanıcı listesine yönlendir
        }
    }
}
