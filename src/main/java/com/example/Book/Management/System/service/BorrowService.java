package com.example.Book.Management.System.service;

import com.example.Book.Management.System.entity.*;
import com.example.Book.Management.System.repository.BookRepository;
import com.example.Book.Management.System.repository.BorrowRecordRepository;
import com.example.Book.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
 import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    // NOTE: You would also inject a StringRedisTemplate here for Rate Limiting.

    private static final Logger logger = LoggerFactory.getLogger(BorrowService.class);

    public BorrowService(BorrowRecordRepository borrowRepo, BookRepository bookRepo, UserRepository userRepo) {
        this.borrowRepo = borrowRepo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
    }


    @CacheEvict(value = "books", key = "#bookId")
    public BorrowRecord borrowBook(Long userId, Long bookId) {
         Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (!book.getStatus().equals(BookStatus.AVAILABLE)) {
            throw new RuntimeException("Book is not available");
         // 1. Setup MDC Context
        MDC.put("operation", "BORROW_BOOK");
        MDC.put("userId", String.valueOf(userId));
        MDC.put("bookId", String.valueOf(bookId));
        logger.info("Starting book borrow attempt for user {} and book {}.", userId, bookId);

        try {
            // NOTE: (Conceptual) Rate Limiting Logic would go here using RedisTemplate
            // if (redisTemplate.opsForValue().increment("rate_limit::" + userId) > 5) { throw new RuntimeException("Rate limit exceeded"); }

            // 2. Fetch and Validate Book
            Book book = bookRepo.findById(bookId)
                    .orElseThrow(() -> {
                        logger.warn("Borrow failed: Book not found with ID: {}", bookId);
                        // NOTE: Recommend using a custom exception class for 404 response
                        return new RuntimeException("Book not found");
                    });

            if (!book.getStatus().equals(BookStatus.AVAILABLE)) {
                logger.warn("Borrow failed: Book ID {} is currently unavailable (Status: {}).",
                        bookId, book.getStatus());
                throw new RuntimeException("Book is not available");
            }

            // 3. Fetch User
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

            // 5. Persist (Status update on book is tracked, record is saved)
            bookRepo.save(book);
            BorrowRecord savedRecord = borrowRepo.save(record);

            logger.info("Book successfully borrowed. New Borrow Record ID: {}.", savedRecord.getId());
            return savedRecord;

        } catch (RuntimeException e) {
            logger.error("Borrow transaction failed: {}", e.getMessage());
            throw e;
        } finally {
            MDC.clear();
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


    @CacheEvict(value = "books", key = "#record.book.id")
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = borrowRepo.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);

        Book book = record.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookRepo.save(book);

         return borrowRepo.save(record);
             record.setReturnDate(LocalDate.now());
            record.setStatus(BorrowStatus.RETURNED);

            // 4. Update Book Status (Ensures the book's cached entry is now stale)
            Book book = record.getBook();
            book.setStatus(BookStatus.AVAILABLE);

            // 5. Persist
            bookRepo.save(book);
            BorrowRecord returnedRecord = borrowRepo.save(record);

            logger.info("Book return processed successfully for record ID {}. Book ID {} now AVAILABLE. Cache for book evicted.",
                    recordId, book.getId());
            return returnedRecord;

        } catch (RuntimeException e) {
            logger.error("Return transaction failed: {}", e.getMessage());
            throw e;
        } finally {
            MDC.clear();
        }
     }
}
