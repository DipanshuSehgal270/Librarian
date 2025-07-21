package com.example.Book.Management.System.controller;

import com.example.Book.Management.System.entity.BorrowRecord;
import com.example.Book.Management.System.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @Operation(
            summary = "Borrow a book",
            description = "Allows a user to borrow a book by providing their user ID and the book ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book borrowed successfully"),
            @ApiResponse(responseCode = "404", description = "User or Book not found"),
            @ApiResponse(responseCode = "400", description = "Book is not available for borrowing")
    })
    @PostMapping("/{userId}/{bookId}")
    public ResponseEntity<BorrowRecord> borrowBook(
            @Parameter(description = "ID of the user borrowing the book", required = true, example = "1")
            @PathVariable Long userId,

            @Parameter(description = "ID of the book to be borrowed", required = true, example = "101")
            @PathVariable Long bookId
    ) {
        return ResponseEntity.ok(borrowService.borrowBook(userId, bookId));
    }

    @Operation(
            summary = "Return a borrowed book",
            description = "Returns a borrowed book by providing the borrow record ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @ApiResponse(responseCode = "404", description = "Borrow record not found")
    })
    @PostMapping("/return/{recordId}")
    public ResponseEntity<BorrowRecord> returnBook(
            @Parameter(description = "ID of the borrow record", required = true, example = "5")
            @PathVariable Long recordId
    ) {
        return ResponseEntity.ok(borrowService.returnBook(recordId));
    }
}
