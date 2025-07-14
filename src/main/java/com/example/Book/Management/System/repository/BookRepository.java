package com.example.Book.Management.System.repository;

import com.example.Book.Management.System.entity.Book;
import com.example.Book.Management.System.entity.BookStatus;
import com.example.Book.Management.System.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByTitleContainingIgnoreCase(@Param("title") String title);

    List<Book> findByAuthorId(Long authorId);
    List<Book> findByPublisherId(Long publisherId);
    List<Book> findByStatus(BookStatus status);

    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId AND b.status = :status")
    List<Book> findByAuthorIdAndStatus(@Param("authorId") Long authorId, @Param("status") BookStatus status);

    Optional<Book> findByTitle(String title);
}

