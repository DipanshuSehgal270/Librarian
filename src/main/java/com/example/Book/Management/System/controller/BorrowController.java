package com.example.Book.Management.System.controller;

import com.example.Book.Management.System.entity.BorrowRecord;
import com.example.Book.Management.System.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("/{userId}/{bookId}")
    public ResponseEntity<BorrowRecord> borrowBook(@PathVariable Long userId, @PathVariable Long bookId) {
        return ResponseEntity.ok(borrowService.borrowBook(userId, bookId));
    }

    @PostMapping("/return/{recordId}")
    public ResponseEntity<BorrowRecord> returnBook(@PathVariable Long recordId) {
        return ResponseEntity.ok(borrowService.returnBook(recordId));
    }
}
