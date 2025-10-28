package com.example.Book.Management.System.service;

import com.example.Book.Management.System.entity.Publisher;
import com.example.Book.Management.System.repository.PublisherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    private static final Logger logger = LoggerFactory.getLogger(PublisherService.class);

    @Transactional(readOnly = true)
    public List<Publisher> getAllPublishers() {
        logger.debug("Attempting to retrieve all publishers.");
        List<Publisher> publishers = publisherRepository.findAll();
        logger.debug("Successfully fetched {} publishers.", publishers.size());
        return publishers;
    }

    @Transactional(readOnly = true)
    public Optional<Publisher> getPublisherById(Long id) {
        logger.info("Starting lookup for publisher by ID: {}", id);
        Optional<Publisher> publisher = publisherRepository.findById(id);

        if (publisher.isPresent()) {
            logger.info("Publisher found with ID: {}", id);
        } else {
            logger.warn("Publisher lookup failed. No entity found for ID: {}", id);
        }
        return publisher;
    }


    public Publisher savePublisher(Publisher publisher) {
        MDC.put("Publisher name" , publisher.getName());
        MDC.put("operation" , "SAVE_PUBLISHER");
        logger.info("Starting save operation for new publisher.");

        // *Flaw Correction:* Your original class was missing validation.
        // Adding a basic check here for robust logging.
        if (publisherRepository.existsByName(publisher.getName())) {
            logger.error("Save failed: Publisher name '{}' already exists.", publisher.getName());
            MDC.clear();
            throw new RuntimeException("Publisher name already exists: " + publisher.getName());
        }

        Publisher savedPublisher = publisherRepository.save(publisher);
        logger.info("Publisher saved successfully with ID: {}", savedPublisher.getId());
        MDC.clear(); // Important: clear MDC after the transaction
        return savedPublisher;
    }

    @Transactional
    public Publisher updatePublisher(Long id, Publisher publisherDetails) {
        MDC.put("targetPublisherId", String.valueOf(id));
        MDC.put("operation", "UPDATE_PUBLISHER");
        logger.info("Attempting to update publisher with ID: {}", id);

        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> {
                    // Log failure before throwing the exception
                    logger.error("Update failed: Publisher not found with ID: {}", id);
                    MDC.clear();
                    return new RuntimeException("Publisher not found with id: " + id);
                });

        // Log the field changes being applied
        logger.debug("Applying updates: Name={}, Email={}, Phone={}",
                publisherDetails.getName(), publisherDetails.getEmail(), publisherDetails.getPhoneNumber());

        publisher.setName(publisherDetails.getName());
        publisher.setEmail(publisherDetails.getEmail());
        publisher.setAddress(publisherDetails.getAddress());
        publisher.setPhoneNumber(publisherDetails.getPhoneNumber());

        Publisher updatedPublisher = publisherRepository.save(publisher);
        logger.info("Publisher updated successfully for ID: {}", updatedPublisher.getId());
        MDC.clear();
        return updatedPublisher;
    }

    public void deletePublisher(Long id) {
        MDC.put("targetPublisherId", String.valueOf(id));
        MDC.put("operation", "DELETE_PUBLISHER");
        logger.info("Starting delete operation for publisher ID: {}", id);

        try {
            // Check existence to log meaningful failure/success messages
            if (!publisherRepository.existsById(id)) {
                logger.warn("Delete aborted. Publisher ID {} does not exist.", id);
                MDC.clear();
                return;
            }

            publisherRepository.deleteById(id);
            logger.info("Publisher successfully deleted with ID: {}", id);
        } catch (Exception e) {
            // Log the exception details and stack trace for operational failure
            logger.error("CRITICAL: Failed to delete publisher ID {}. Error: {}", id, e.getMessage(), e);
        }
        MDC.clear();
    }

    @Transactional(readOnly = true)
    public List<Publisher> searchPublishersByName(String name) {
        logger.info("Searching for publishers with name containing: '{}'", name);
        List<Publisher> publishers = publisherRepository.findByNameContainingIgnoreCase(name);
        logger.info("Search complete. Found {} publishers matching criteria.", publishers.size());
        return publishers;
    }

    @Transactional(readOnly = true)
    public Optional<Publisher> findByEmail(String email) {
        logger.debug("Looking up publisher by email: {}", email);
        return publisherRepository.findByEmail(email);
    }
}
