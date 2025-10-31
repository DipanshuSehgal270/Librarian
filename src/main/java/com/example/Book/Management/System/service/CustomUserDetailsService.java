package com.example.Book.Management.System.service;

import com.example.Book.Management.System.entity.User;
import com.example.Book.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
<<<<<<< Updated upstream
=======
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cache.annotation.Cacheable;
>>>>>>> Stashed changes
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
//    @Cacheable(value = "userDetailsCache", key = "#username") // ⬅️ The Redis implementation
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
<<<<<<< Updated upstream
=======
        MDC.put("loginUsername", username);
        logger.debug("Attempting to load security details for username: {}", username);

        // This code only runs if the entry is NOT found in the Redis cache.
>>>>>>> Stashed changes
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}

