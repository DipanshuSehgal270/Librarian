package com.example.Book.Management.System.controller;

import com.example.Book.Management.System.entity.Author;
import com.example.Book.Management.System.service.AuthorService;
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
@RequestMapping("/api/authors")
@CrossOrigin(origins = "*")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    @Operation(summary = "Get all authors", description = "Returns a list of all authors available in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of authors"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Author>> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Retrieves a specific author using their unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author found"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<Author> getAuthorById(
            @Parameter(description = "ID of the author to retrieve") @PathVariable Long id) {
        Optional<Author> author = authorService.getAuthorById(id);
        return author.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new author", description = "Creates a new author entry in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Author created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody Author author) {
        try {
            Author savedAuthor = authorService.saveAuthor(author);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAuthor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing author", description = "Updates the information of an existing author using their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author updated successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<Author> updateAuthor(
            @Parameter(description = "ID of the author to update") @PathVariable Long id,
            @Valid @RequestBody Author author) {
        try {
            Author updatedAuthor = authorService.updateAuthor(id, author);
            return ResponseEntity.ok(updatedAuthor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an author", description = "Deletes the author with the specified ID from the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<Void> deleteAuthor(
            @Parameter(description = "ID of the author to delete") @PathVariable Long id) {
        try {
            authorService.deleteAuthor(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search authors by name", description = "Returns a list of authors whose name matches the given keyword.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authors found"),
            @ApiResponse(responseCode = "404", description = "No authors match the search criteria")
    })
    public ResponseEntity<List<Author>> searchAuthors(
            @Parameter(description = "Name or part of name to search") @RequestParam String name) {
        List<Author> authors = authorService.searchAuthorsByName(name);
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get author by email", description = "Retrieves the author associated with the given email address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author found"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<Author> getAuthorByEmail(
            @Parameter(description = "Email address of the author to retrieve") @PathVariable String email) {
        Optional<Author> author = authorService.findByEmail(email);
        return author.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
