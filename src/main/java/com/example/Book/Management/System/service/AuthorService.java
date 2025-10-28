package com.example.Book.Management.System.service;

import com.example.Book.Management.System.entity.Author;
import com.example.Book.Management.System.repository.AuthorRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository)
    {
        this.authorRepository = authorRepository;
    }

    public List<Author> getAllAuthors() {
        log.debug("Fetching all authors.");
        return authorRepository.findAll();
    }

    public Optional<Author> getAuthorById(Long id) {
        MDC.put("authorId", String.valueOf(id));
        log.info("Starting lookup for author by ID.");
        Optional<Author> author = authorRepository.findById(id);
        if (author.isEmpty()) {
            log.warn("Author not found for ID.");
        }
        MDC.clear();
        return author;
    }

    @Transactional // Override to enable write/rollback behavior
    public Author saveAuthor(Author author) {
        MDC.put("operation", "SAVE_AUTHOR");
        log.info("Starting save operation for new author: {}", author.getName());

        // *Add business logic check (e.g., uniqueness) for robustness*
        if (authorRepository.existsByEmail(author.getEmail())) {
            log.error("Save failed: Email '{}' already exists.", author.getEmail());
            MDC.clear();
            throw new RuntimeException("Email already exists: " + author.getEmail());
        }

        Author savedAuthor = authorRepository.save(author);
        log.info("Author saved successfully with ID: {}", savedAuthor.getId());
        MDC.clear();
        return savedAuthor;
    }

    @Transactional // Override to enable write/rollback behavior
    public Author updateAuthor(Long id, Author authorDetails) {
        MDC.put("operation", "UPDATE_AUTHOR");
        MDC.put("authorId", String.valueOf(id));
        log.info("Starting update for author ID {}.", id);

        try {
            Author author = authorRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Update failed: Author ID not found.");
                        return new RuntimeException("Author not found with id: " + id);
                    });

            // Log the key changes
            log.debug("Applying updates: Email={}, BirthDate={}",
                    authorDetails.getEmail(), authorDetails.getBirthDate());

            author.setName(authorDetails.getName());
            author.setEmail(authorDetails.getEmail());
            author.setBirthDate(authorDetails.getBirthDate());
            author.setBiography(authorDetails.getBiography());

            Author updatedAuthor = authorRepository.save(author);
            log.info("Author updated successfully.");
            return updatedAuthor;
        } finally {
            MDC.clear();
        }
    }

    @Transactional // Override to enable write/rollback behavior and handle exceptions
    public void deleteAuthor(Long id) {
        MDC.put("operation", "DELETE_AUTHOR");
        MDC.put("authorId", String.valueOf(id));
        log.info("Attempting to delete author by ID.");

        try {
            if (!authorRepository.existsById(id)) {
                log.warn("Delete skipped: Author ID not found.");
                return;
            }
            authorRepository.deleteById(id);
            log.info("Author deleted successfully.");
        } catch (Exception e) {
            // Log the exception details for foreign key constraint failures
            log.error("CRITICAL: Failed to delete author ID {}. Error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete author due to internal error.", e);
        } finally {
            MDC.clear();
        }
    }

    public List<Author> searchAuthorsByName(String name) {
        log.info("Searching for authors by name containing: '{}'", name);
        return authorRepository.findByNameContainingIgnoreCase(name);
    }

    public Optional<Author> findByEmail(String email) {
        log.debug("Looking up author by email: {}", email);
        return authorRepository.findByEmail(email);
    }
}
