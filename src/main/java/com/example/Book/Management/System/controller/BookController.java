package com.example.Book.Management.System.controller;

import com.example.Book.Management.System.dto.BookDto;
import com.example.Book.Management.System.entity.BookStatus;
import com.example.Book.Management.System.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books", description = "Returns a paginated and sorted list of all available books in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<BookDto>> getAllBooks(
            @Parameter(description = "Page number to retrieve") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "title") String sortBy) {
        Page<BookDto> books = bookService.getAllBooks(page, size, sortBy);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID", description = "Retrieve a book's details using its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookDto> getBookById(
            @Parameter(description = "ID of the book to retrieve") @PathVariable Long id) {
        Optional<BookDto> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new book", description = "Add a new book to the system. Accepts BookDto as JSON.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        try {
            BookDto savedBook = bookService.saveBook(bookDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing book", description = "Update a book's details by providing its ID and new data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookDto> updateBook(
            @Parameter(description = "ID of the book to update") @PathVariable Long id,
            @Valid @RequestBody BookDto bookDto) {
        try {
            BookDto updatedBook = bookService.updateBook(id, bookDto);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a book", description = "Delete a book from the system using its ID. Only Admins can perform this.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID of the book to delete") @PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search books by title", description = "Find books with titles containing the given keyword.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No books found")
    })
    public ResponseEntity<List<BookDto>> searchBooks(
            @Parameter(description = "Book title or keyword to search") @RequestParam String title) {
        List<BookDto> books = bookService.searchBooksByTitle(title);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "Search book by ISBN", description = "Retrieve a book using its unique ISBN number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookDto> getBookByIsbn(
            @Parameter(description = "ISBN of the book to search") @PathVariable String isbn) {
        Optional<BookDto> book = bookService.findByIsbn(isbn);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get books by author", description = "Fetch all books written by a specific author using their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No books found for the author")
    })
    public ResponseEntity<List<BookDto>> getBooksByAuthor(
            @Parameter(description = "Author's ID") @PathVariable Long authorId) {
        List<BookDto> books = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/publisher/{publisherId}")
    @Operation(summary = "Get books by publisher", description = "Fetch all books published by a specific publisher using their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No books found for the publisher")
    })
    public ResponseEntity<List<BookDto>> getBooksByPublisher(
            @Parameter(description = "Publisher's ID") @PathVariable Long publisherId) {
        List<BookDto> books = bookService.getBooksByPublisher(publisherId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get books by status", description = "Retrieve books based on their status (e.g., AVAILABLE, BORROWED).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No books found with given status")
    })
    public ResponseEntity<List<BookDto>> getBooksByStatus(
            @Parameter(description = "Status of the book (AVAILABLE, BORROWED, etc.)") @PathVariable BookStatus status) {
        List<BookDto> books = bookService.getBooksByStatus(status);
        return ResponseEntity.ok(books);
    }
}
