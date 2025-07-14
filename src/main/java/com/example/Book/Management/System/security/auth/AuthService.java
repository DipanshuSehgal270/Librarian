package com.example.Book.Management.System.security.auth;

import com.example.Book.Management.System.entity.UserRole;
import com.example.Book.Management.System.security.auth.AuthRequest;
import com.example.Book.Management.System.security.auth.AuthResponse;
import com.example.Book.Management.System.security.auth.RegisterRequest;
import com.example.Book.Management.System.entity.User;
import com.example.Book.Management.System.repository.UserRepository;
import com.example.Book.Management.System.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private  UserRole userRole;

    public void register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : UserRole.USER);

        userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtUtils.generateToken(request.getUsername());
        return new AuthResponse(token);
    }
}

