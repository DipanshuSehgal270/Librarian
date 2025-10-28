package com.example.Book.Management.System.service;

import com.example.Book.Management.System.entity.User;
import com.example.Book.Management.System.entity.UserRole;
import com.example.Book.Management.System.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<User> getAllUsers() {
        logger.debug("Attempting to fetch all users.");
        List<User> users = userRepository.findAll();
        logger.debug("Successfully fetched {} users.", users.size());
        return users;
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Attempting to find user by ID: {}", id); // Info for specific ID lookups
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.info("Found user with ID: {}", id);
        } else {
            logger.warn("User not found with ID: {}", id);
        }
        return user;
    }

    public User saveUser(User user) {

        logger.info("Starting user creation for username: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            logger.error("User creation failed: Username '{}' already exists.", user.getUsername());
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.error("User creation failed: Email '{}' already exists.", user.getEmail());
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    public User updateUser(Long id, User userDetails) {

        logger.info("Attempting to update user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    // Log the failure to find before throwing the exception
                    logger.error("Update failed: User not found with ID: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });

        logger.debug("Updating user ID {} fields: email={}, role={}",
                id, userDetails.getEmail(), userDetails.getRole());

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully for ID: {}", id);
        return updatedUser;
    }

    public void deleteUser(Long id) {
        logger.info("Attempting to delete user by ID: {}", id);

        try {
            // Check existence first to log a meaningful message
            if (!userRepository.existsById(id)) {
                logger.warn("Delete skipped: User not found with ID: {}", id);
                return; // Exit gracefully if not found
            }

            userRepository.deleteById(id);
            logger.info("User deleted successfully with ID: {}", id);
        } catch (Exception e) {
            // Log the exception details for actual operational failure
            logger.error("Failed to delete user ID {}. Error: {}", id, e.getMessage(), e); // Include 'e' for stack trace
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByRole(UserRole role) {
            return userRepository.findByRole(role);
    }
}
