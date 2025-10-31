package com.example.Book.Management.System.service;

import com.example.Book.Management.System.dto.BookDto;
import com.example.Book.Management.System.entity.Book;
import com.example.Book.Management.System.entity.BookStatus;
import com.example.Book.Management.System.entity.Author;
import com.example.Book.Management.System.entity.Publisher;
import com.example.Book.Management.System.exception.BookNotFoundException;
import com.example.Book.Management.System.repository.BookRepository;
import com.example.Book.Management.System.repository.AuthorRepository;
import com.example.Book.Management.System.repository.PublisherRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<BookDto> getAllBooks(int pageNumber , int pageSize , String feild) {
        Pageable pageable =  PageRequest.of(pageNumber , pageSize , Sort.by(feild));
        return bookRepository.findAll(pageable)
                .map(book -> modelMapper.map(book, BookDto.class));
    }

    @Cacheable(value = "Books" , key = "#id")
    public Optional<BookDto> getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::convertToDto);

        MDC.put("bookId", String.valueOf(id));
        logger.info("Attempting to find book by ID (Cache Miss - hitting DB).");

        Optional<BookDto> dto = bookRepository.findById(id)
                .map(book -> modelMapper.map(book, BookDto.class));

        if (dto.isEmpty()) {
            logger.warn("Book not found for ID.");
        }
        MDC.clear();
        return dto;
    }

    public BookDto saveBook(BookDto bookDto) {
        Book book = convertToEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return convertToDto(savedBook);
    }

    @Transactional
    @CachePut(value = "Books",key = "#id")
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        book.settitle(bookDto.getTitle());
        book.setIsbn(bookDto.getIsbn());
        book.setDescription(bookDto.getDescription());
        book.setPublicationDate(bookDto.getPublicationDate());
        book.setPageCount(bookDto.getPageCount());
        book.setPrice(bookDto.getPrice());
        book.setStatus(bookDto.getStatus());
        book.setCoverImageUrl(bookDto.getCoverImageUrl());
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Update failed: Book ID not found.");
                        return new BookNotFoundException(id);
                    });

        if (bookDto.getAuthorId() != null) {
            Author author = authorRepository.findById(bookDto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found with id: " + bookDto.getAuthorId()));
            book.setAuthor(author);
        }

        if (bookDto.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(bookDto.getPublisherId())
                    .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + bookDto.getPublisherId()));
            book.setPublisher(publisher);
        }

        Book updatedBook = bookRepository.save(book);
        return convertToDto(updatedBook);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    @Transactional // Must be transactional: handles potential exceptions like foreign key constraints
    @CacheEvict(value = "books" , key = "#id")
    public void deleteBook(Long id) {

        MDC.put("operation", "DELETE_BOOK");
        MDC.put("bookId", String.valueOf(id));
        logger.info("Attempting to delete book by ID. Cache entry will be evicted.");

        try {
            if (!bookRepository.existsById(id)) {
                logger.warn("Delete skipped: Book ID not found.");
                return;
            }
            bookRepository.deleteById(id);
            logger.info("Book deleted successfully.");
        } catch (Exception e) {
            // Log the exception details and stack trace
            logger.error("CRITICAL: Failed to delete book ID {}. Error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete book due to internal error.", e);
        } finally {
            MDC.clear();
        }
    }

    public List<BookDto> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByPublisher(Long publisherId) {
        return bookRepository.findByPublisherId(publisherId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByStatus(BookStatus status) {
        return bookRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<BookDto> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(this::convertToDto);
    }

    private BookDto convertToDto(Book book) {
        BookDto dto = new BookDto();
        dto.setTitle(book.gettitle());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setPageCount(book.getPageCount());
        dto.setPrice(book.getPrice());
        dto.setStatus(book.getStatus());
        dto.setCoverImageUrl(book.getCoverImageUrl());
        dto.setAuthorId(book.getAuthor().getId());
        dto.setAuthorName(book.getAuthor().getName());
        dto.setPublisherId(book.getPublisher().getId());
        dto.setPublisherName(book.getPublisher().getName());
        return dto;
    }

    private Book convertToEntity(BookDto dto) {
        Book book = new Book();
        book.settitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setPublicationDate(dto.getPublicationDate());
        book.setPageCount(dto.getPageCount());
        book.setPrice(dto.getPrice());
        book.setStatus(dto.getStatus() != null ? dto.getStatus() : BookStatus.AVAILABLE);
        book.setCoverImageUrl(dto.getCoverImageUrl());

    public Book convertToEntity(BookDto dto) {
        Book book = modelMapper.map(dto, Book.class); // Map common fields

//         Handle Author lookup and association
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + dto.getAuthorId()));
        book.setAuthor(author);

        Publisher publisher = publisherRepository.findById(dto.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + dto.getPublisherId()));
        book.setPublisher(publisher);


//         Set default status if not provided (good business logic check)
        if (book.getStatus() == null) {
            book.setStatus(BookStatus.AVAILABLE);
        }

        return book;
    }
}
