package com.example.Book.Management.System.service;

import com.example.Book.Management.System.dto.BookDto;
import com.example.Book.Management.System.entity.Book;
import com.example.Book.Management.System.entity.BookStatus;
import com.example.Book.Management.System.entity.Author;
import com.example.Book.Management.System.entity.Publisher;
import com.example.Book.Management.System.repository.BookRepository;
import com.example.Book.Management.System.repository.AuthorRepository;
import com.example.Book.Management.System.repository.PublisherRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository,
                       PublisherRepository publisherRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.modelMapper = modelMapper;
    }

    public Page<BookDto> getAllBooks(int pageNumber , int pageSize , String feild) {
        logger.debug("Fetching books page {} with size {} sorted by {}.", pageNumber, pageSize, feild);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(feild));

        return bookRepository.findAll(pageable)
                .map(book -> modelMapper.map(book, BookDto.class));
    }

    public Optional<BookDto> getBookById(Long id) {
        MDC.put("bookId", String.valueOf(id));
        logger.info("Attempting to find book by ID.");
        Optional<BookDto> dto = bookRepository.findById(id)
                .map(book -> modelMapper.map(book, BookDto.class));

        if (dto.isEmpty()) {
            logger.warn("Book not found for ID.");
        }
        MDC.clear();
        return dto;
    }

    @Transactional
    public BookDto saveBook(BookDto bookDto) {
        MDC.put("operation", "SAVE_BOOK");
        logger.info("Starting new book creation for title: {}", bookDto.getTitle());

        try {
            Book book = convertToEntity(bookDto); // This method includes lookups
            Book savedBook = bookRepository.save(book);
            logger.info("Book saved successfully with ID: {}", savedBook.getId());
            return modelMapper.map(savedBook, BookDto.class);
        } finally {
            MDC.clear();
        }
    }

    @Transactional
    public BookDto updateBook(Long id, BookDto bookDto) {
        MDC.put("operation", "UPDATE_BOOK");
        MDC.put("bookId", String.valueOf(id));
        logger.info("Starting update for book ID {}.", id);

        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Update failed: Book ID not found.");
                        return new RuntimeException("Book not found with id: " + id);
                    });

            // Log major changes (DEBUG level)
            logger.debug("Updating fields: ISBN={}, Title={}", bookDto.getIsbn(), bookDto.getTitle());

            // 1. Update basic fields
            // NOTE: Use ModelMapper here for a cleaner update if possible, e.g., modelMapper.map(bookDto, book);
            // Assuming you must set manually due to partial updates:
            book.settitle(bookDto.getTitle());
            book.setIsbn(bookDto.getIsbn());
            book.setDescription(bookDto.getDescription());
            // ... (other fields)

            // 2. Update Author (requires transaction for lookup/association)
            if (bookDto.getAuthorId() != null) {
                Author author = authorRepository.findById(bookDto.getAuthorId())
                        .orElseThrow(() -> new RuntimeException("Author not found with id: " + bookDto.getAuthorId()));
                book.setAuthor(author);
            }

            // 3. Update Publisher (requires transaction for lookup/association)
            if (bookDto.getPublisherId() != null) {
                Publisher publisher = publisherRepository.findById(bookDto.getPublisherId())
                        .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + bookDto.getPublisherId()));
                book.setPublisher(publisher);
            }

            Book updatedBook = bookRepository.save(book);
            logger.info("Book updated successfully.");
            return modelMapper.map(updatedBook, BookDto.class);
        } finally {
            MDC.clear();
        }
    }

    @Transactional // Must be transactional: handles potential exceptions like foreign key constraints
    public void deleteBook(Long id) {
        MDC.put("operation", "DELETE_BOOK");
        MDC.put("bookId", String.valueOf(id));
        logger.info("Attempting to delete book by ID.");

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
        logger.info("Searching for books by title containing: '{}'", title);
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(book -> modelMapper.map(book, BookDto.class))
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
        dto.setId(book.getId());
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
        Book book = modelMapper.map(dto, Book.class); // Map common fields

        // Handle Author lookup and association
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + dto.getAuthorId()));
        book.setAuthor(author);

        // Handle Publisher lookup and association
        Publisher publisher = publisherRepository.findById(dto.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + dto.getPublisherId()));
        book.setPublisher(publisher);

        // Set default status if not provided (good business logic check)
        if (book.getStatus() == null) {
            book.setStatus(BookStatus.AVAILABLE);
        }

        return book;
    }
}
