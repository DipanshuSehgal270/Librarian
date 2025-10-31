package com.example.Book.Management.System.controller;

import com.example.Book.Management.System.dto.BookDto;
import com.example.Book.Management.System.entity.BookStatus;
import com.example.Book.Management.System.service.BookService;
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

    //TODO SOLVING THE ERROR FOR FETCHING ALL BOOKS
    @GetMapping
//    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Page<BookDto>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "5") int size,
                                                     @RequestParam(defaultValue = "title") String sortBy) {
        Page<BookDto> books = bookService.getAllBooks(page,size,sortBy);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        Optional<BookDto> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
<<<<<<< Updated upstream
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
=======
    @Operation(summary = "Create a new book", description = "Add a new book to the system. Accepts BookDto as JSON.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<BookDto> createBook(@RequestBody @Valid BookDto bookDto) {
>>>>>>> Stashed changes
        try {
            BookDto savedBook = bookService.saveBook(bookDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        try {
            BookDto updatedBook = bookService.updateBook(id, bookDto);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> searchBooks(@RequestParam String title) {
        List<BookDto> books = bookService.searchBooksByTitle(title);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDto> getBookByIsbn(@PathVariable String isbn) {
        Optional<BookDto> book = bookService.findByIsbn(isbn);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<BookDto>> getBooksByAuthor(@PathVariable Long authorId) {
        List<BookDto> books = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/publisher/{publisherId}")
    public ResponseEntity<List<BookDto>> getBooksByPublisher(@PathVariable Long publisherId) {
        List<BookDto> books = bookService.getBooksByPublisher(publisherId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookDto>> getBooksByStatus(@PathVariable BookStatus status) {
        List<BookDto> books = bookService.getBooksByStatus(status);
        return ResponseEntity.ok(books);
    }
}