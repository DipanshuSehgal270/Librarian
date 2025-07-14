package com.example.Book.Management.System.service;

import com.example.Book.Management.System.entity.Publisher;
import com.example.Book.Management.System.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public Optional<Publisher> getPublisherById(Long id) {
        return publisherRepository.findById(id);
    }

    public Publisher savePublisher(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    public Publisher updatePublisher(Long id, Publisher publisherDetails) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + id));

        publisher.setName(publisherDetails.getName());
        publisher.setEmail(publisherDetails.getEmail());
        publisher.setAddress(publisherDetails.getAddress());
        publisher.setPhoneNumber(publisherDetails.getPhoneNumber());

        return publisherRepository.save(publisher);
    }

    public void deletePublisher(Long id) {
        publisherRepository.deleteById(id);
    }

    public List<Publisher> searchPublishersByName(String name) {
        return publisherRepository.findByNameContainingIgnoreCase(name);
    }

    public Optional<Publisher> findByEmail(String email) {
        return publisherRepository.findByEmail(email);
    }
}
