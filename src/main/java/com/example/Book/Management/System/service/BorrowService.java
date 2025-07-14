package com.example.Book.Management.System.service;

import com.example.Book.Management.System.entity.*;
import com.example.Book.Management.System.repository.BookRepository;
import com.example.Book.Management.System.repository.BorrowRecordRepository;
import com.example.Book.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    public BorrowRecord borrowBook(Long userId, Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (!book.getStatus().equals(BookStatus.AVAILABLE)) {
            throw new RuntimeException("Book is not available");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setUser(user);
        record.setBorrowDate(LocalDate.now());
        record.setStatus(BorrowStatus.BORROWED);

        book.setStatus(BookStatus.BORROWED);
        bookRepo.save(book);

        return borrowRepo.save(record);
    }

    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = borrowRepo.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);

        Book book = record.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookRepo.save(book);

        return borrowRepo.save(record);
    }
}
