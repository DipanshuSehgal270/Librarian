package com.example.Book.Management.System.repository;

import com.example.Book.Management.System.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByEmail(String email);

    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT a FROM Author a JOIN a.books b GROUP BY a.id ORDER BY COUNT(b) DESC")
    List<Author> findAuthorsOrderByBookCount();

    boolean existsByEmail(String email);
}

