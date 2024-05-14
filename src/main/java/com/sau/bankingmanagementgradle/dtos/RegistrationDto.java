package com.sau.bankingmanagementgradle.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDto {
    private int id;
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    @NotEmpty(message = "Please confirm your password")
    private String rePassword;
    // Custom validation method to check if password and rePassword match
    public boolean isPasswordMatch() {
        return password != null && password.equals(rePassword);
    }
}
