package com.example.Book.Management.System.controller;

import com.example.Book.Management.System.entity.Publisher;
import com.example.Book.Management.System.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/publishers")
@CrossOrigin(origins = "*")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        List<Publisher> publishers = publisherService.getAllPublishers();
        return ResponseEntity.ok(publishers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisherById(@PathVariable Long id) {
        Optional<Publisher> publisher = publisherService.getPublisherById(id);
        return publisher.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Publisher> createPublisher(@Valid @RequestBody Publisher publisher) {
        try {
            Publisher savedPublisher = publisherService.savePublisher(publisher);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPublisher);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publisher> updatePublisher(@PathVariable Long id, @Valid @RequestBody Publisher publisher) {
        try {
            Publisher updatedPublisher = publisherService.updatePublisher(id, publisher);
            return ResponseEntity.ok(updatedPublisher);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        try {
            publisherService.deletePublisher(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Publisher>> searchPublishers(@RequestParam String name) {
        List<Publisher> publishers = publisherService.searchPublishersByName(name);
        return ResponseEntity.ok(publishers);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Publisher> getPublisherByEmail(@PathVariable String email) {
        Optional<Publisher> publisher = publisherService.findByEmail(email);
        return publisher.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
