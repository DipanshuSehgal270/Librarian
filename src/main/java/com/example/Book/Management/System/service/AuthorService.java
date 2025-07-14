package com.example.Book.Management.System.service;

import com.example.Book.Management.System.entity.Author;
import com.example.Book.Management.System.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));

        author.setName(authorDetails.getName());
        author.setEmail(authorDetails.getEmail());
        author.setBirthDate(authorDetails.getBirthDate());
        author.setBiography(authorDetails.getBiography());

        return authorRepository.save(author);
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    public List<Author> searchAuthorsByName(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name);
    }

    public Optional<Author> findByEmail(String email) {
        return authorRepository.findByEmail(email);
    }
}
