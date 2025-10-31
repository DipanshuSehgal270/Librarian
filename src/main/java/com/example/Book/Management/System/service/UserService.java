package com.example.Book.Management.System.service;

import com.example.Book.Management.System.entity.User;
import com.example.Book.Management.System.entity.UserRole;
import com.example.Book.Management.System.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;


    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // NOTE: Caching all users is generally avoided.
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {

        return userRepository.findById(id);
        logger.info("Attempting to find user by ID (Cache Miss - hitting DB): {}", id);

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.info("Found user with ID: {}", id);
        } else {
            logger.warn("User not found with ID: {}", id);
        }
        return user;
    }

    /**
     * Cache is NOT put here. A subsequent getUserById() will establish the cache entry.
     */
    public User saveUser(User user) {

        logger.info("Starting user creation for username: {}", user.getUsername());


        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }


    @CachePut(value = "users", key = "#id")
    @Transactional
    public User updateUser(Long id, User userDetails) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        logger.info("Attempting to update user ID: {}. Cache entry will be updated.", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Update failed: User not found with ID: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });

        logger.debug("Updating user ID {} fields: email={}, role={}",
                id, userDetails.getEmail(), userDetails.getRole());

        // Update fields
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());

        return userRepository.save(user);
        // Note: save() is redundant here due to @Transactional and Dirty Checking, but harmless.
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully for ID: {}. Cache overwritten.", id);
        return updatedUser; // This updated object is placed in the cache.
    }


    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        logger.info("Attempting to delete user by ID: {}. Cache entry will be evicted.", id);

        try {
            if (!userRepository.existsById(id)) {
                logger.warn("Delete skipped: User not found with ID: {}", id);
                return; // Exit gracefully if not found
            }

            userRepository.deleteById(id);
            logger.info("User deleted successfully with ID: {}. Cache evicted.", id);
        } catch (Exception e) {
            logger.error("Failed to delete user ID {}. Error: {}", id, e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
}
