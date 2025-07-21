package com.example.Book.Management.System.controller;

import com.example.Book.Management.System.entity.User;
import com.example.Book.Management.System.entity.UserRole;
import com.example.Book.Management.System.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Retrieve a list of all users in the system.")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID", description = "Fetch a user by their unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID of the user", required = true, example = "1")
            @PathVariable Long id
    ) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new user", description = "Add a new user to the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "User object to be created", required = true)
            @Valid @RequestBody User user
    ) {
        try {
            User savedUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update existing user", description = "Update user details using their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID of the user to update", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Updated user object", required = true)
            @Valid @RequestBody User user
    ) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete user", description = "Remove a user from the system using their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true, example = "1")
            @PathVariable Long id
    ) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get user by username", description = "Retrieve a user based on their username.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "Username of the user", required = true, example = "john_doe")
            @PathVariable String username
    ) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user by email", description = "Retrieve a user based on their email address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Email of the user", required = true, example = "john@example.com")
            @PathVariable String email
    ) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get users by role", description = "Retrieve users who have a specific role (e.g. ADMIN, USER).")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(
            @Parameter(description = "Role of users to fetch", required = true, example = "USER")
            @PathVariable UserRole role
    ) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
}