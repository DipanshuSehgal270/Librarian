package com.example.Book.Management.System.repository;

import com.example.Book.Management.System.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findByEmail(String email);

    @Query("SELECT p FROM Publisher p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Publisher> findByNameContainingIgnoreCase(@Param("name") String name);

    Optional<Publisher> findByName(String bantamBooks);
}
