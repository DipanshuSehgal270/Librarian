package com.example.Book.Management.System.security.auth;

import com.example.Book.Management.System.entity.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
}

