package com.example.Book.Management.System.controller;

import com.example.Book.Management.System.entity.Publisher;
import com.example.Book.Management.System.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get all publishers", description = "Returns a list of all publishers in the system.")
    @ApiResponse(responseCode = "200", description = "Publishers retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        List<Publisher> publishers = publisherService.getAllPublishers();
        return ResponseEntity.ok(publishers);
    }

    @Operation(summary = "Get publisher by ID", description = "Fetch a single publisher using its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Publisher found"),
            @ApiResponse(responseCode = "404", description = "Publisher not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisherById(
            @Parameter(description = "ID of the publisher", required = true, example = "1")
            @PathVariable Long id
    ) {
        Optional<Publisher> publisher = publisherService.getPublisherById(id);
        return publisher.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new publisher", description = "Add a new publisher to the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Publisher created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid publisher data")
    })
    @PostMapping
    public ResponseEntity<Publisher> createPublisher(
            @Parameter(description = "Publisher object to create", required = true)
            @Valid @RequestBody Publisher publisher
    ) {
        try {
            Publisher savedPublisher = publisherService.savePublisher(publisher);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPublisher);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update an existing publisher", description = "Update the details of a publisher using its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Publisher updated successfully"),
            @ApiResponse(responseCode = "404", description = "Publisher not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Publisher> updatePublisher(
            @Parameter(description = "ID of the publisher to update", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Updated publisher object", required = true)
            @Valid @RequestBody Publisher publisher
    ) {
        try {
            Publisher updatedPublisher = publisherService.updatePublisher(id, publisher);
            return ResponseEntity.ok(updatedPublisher);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a publisher", description = "Remove a publisher from the system by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Publisher deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Publisher not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(
            @Parameter(description = "ID of the publisher to delete", required = true, example = "1")
            @PathVariable Long id
    ) {
        try {
            publisherService.deletePublisher(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Search publishers by name", description = "Find all publishers that match the given name.")
    @ApiResponse(responseCode = "200", description = "Search results returned successfully")
    @GetMapping("/search")
    public ResponseEntity<List<Publisher>> searchPublishers(
            @Parameter(description = "Name keyword to search for", required = true, example = "O'Reilly")
            @RequestParam String name
    ) {
        List<Publisher> publishers = publisherService.searchPublishersByName(name);
        return ResponseEntity.ok(publishers);
    }

    @Operation(summary = "Get publisher by email", description = "Retrieve a publisher using its email address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Publisher found"),
            @ApiResponse(responseCode = "404", description = "Publisher not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Publisher> getPublisherByEmail(
            @Parameter(description = "Email address of the publisher", required = true, example = "info@publisher.com")
            @PathVariable String email
    ) {
        Optional<Publisher> publisher = publisherService.findByEmail(email);
        return publisher.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}