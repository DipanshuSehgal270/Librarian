package com.example.Book.Management.System.service;

import com.example.Book.Management.System.entity.*;
import com.example.Book.Management.System.repository.BookRepository;
import com.example.Book.Management.System.repository.BorrowRecordRepository;
import com.example.Book.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class BorrowService {

    private final BorrowRecordRepository borrowRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;
    private static final Logger logger = LoggerFactory.getLogger(BorrowService.class);

    public BorrowService(BorrowRecordRepository borrowRepo, BookRepository bookRepo, UserRepository userRepo) {
        this.borrowRepo = borrowRepo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
    }

    public BorrowRecord borrowBook(Long userId, Long bookId) {
        // 1. Setup MDC Context
        MDC.put("operation", "BORROW_BOOK");
        MDC.put("userId", String.valueOf(userId));
        MDC.put("bookId", String.valueOf(bookId));
        logger.info("Starting book borrow attempt for user {} and book {}.", userId, bookId);

        try {
            // 2. Fetch and Validate Book
            Book book = bookRepo.findById(bookId)
                    .orElseThrow(() -> {
                        logger.warn("Borrow failed: Book not found with ID: {}", bookId);
                        return new RuntimeException("Book not found");
                    });

            if (!book.getStatus().equals(BookStatus.AVAILABLE)) {
                logger.warn("Borrow failed: Book ID {} is currently unavailable (Status: {}).",
                        bookId, book.getStatus());
                throw new RuntimeException("Book is not available");
            }

            // 3. Fetch User (Essential for validation, though assumed present here)
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> {
                        logger.warn("Borrow failed: User not found with ID: {}", userId);
                        return new RuntimeException("User not found");
                    });

            // 4. Create Record and Update Book Status
            BorrowRecord record = new BorrowRecord();
            record.setBook(book);
            record.setUser(user);
            record.setBorrowDate(LocalDate.now());
            record.setStatus(BorrowStatus.BORROWED);

            book.setStatus(BookStatus.BORROWED);

            // 5. Persist (changes will be flushed atomically by the transaction)
            // Note: If using JPA, the status update on 'book' is tracked automatically.
            bookRepo.save(book);
            BorrowRecord savedRecord = borrowRepo.save(record);

            logger.info("Book successfully borrowed. New Borrow Record ID: {}.", savedRecord.getId());
            return savedRecord;

        } catch (RuntimeException e) {
            // Log the business exception that caused the rollback
            logger.error("Borrow transaction failed: {}", e.getMessage());
            throw e; // Re-throw to trigger the declarative rollback
        } finally {
            MDC.clear(); // Clean up context, regardless of success or failure
        }
    }

    public BorrowRecord returnBook(Long recordId) {
        MDC.put("operation", "RETURN_BOOK");
        MDC.put("recordId", String.valueOf(recordId));
        logger.info("Starting book return process for record ID {}.", recordId);

        try {
            // 2. Fetch Borrow Record
            BorrowRecord record = borrowRepo.findById(recordId)
                    .orElseThrow(() -> {
                        logger.warn("Return failed: Borrow record not found with ID: {}", recordId);
                        return new RuntimeException("Borrow record not found");
                    });

            // 3. Update Record Status
            if (record.getStatus().equals(BorrowStatus.RETURNED)) {
                logger.warn("Return skipped: Record ID {} is already marked as returned.", recordId);
                throw new RuntimeException("Book is already marked as returned");
            }

            record.setReturnDate(LocalDate.now());
            record.setStatus(BorrowStatus.RETURNED);

            // 4. Update Book Status
            Book book = record.getBook();
            book.setStatus(BookStatus.AVAILABLE);
            // bookRepo.save(book); // Can often be omitted, but included for clarity

            // 5. Persist (changes flushed atomically)
            bookRepo.save(book);
            BorrowRecord returnedRecord = borrowRepo.save(record);

            logger.info("Book return processed successfully for record ID {}. Book ID {} now AVAILABLE.",
                    recordId, book.getId());
            return returnedRecord;

        } catch (RuntimeException e) {
            // Log the exception that caused the rollback
            logger.error("Return transaction failed: {}", e.getMessage());
            throw e; // Re-throw to trigger the declarative rollback
        } finally {
            MDC.clear(); // Clean up context
        }
    }
}
