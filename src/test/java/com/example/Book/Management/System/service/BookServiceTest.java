package com.example.Book.Management.System.service;

import com.example.Book.Management.System.dto.BookDto;
import com.example.Book.Management.System.entity.Author;
import com.example.Book.Management.System.entity.Book;
import com.example.Book.Management.System.entity.Publisher;
import com.example.Book.Management.System.repository.AuthorRepository;
import com.example.Book.Management.System.repository.BookRepository;
import com.example.Book.Management.System.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import static com.example.Book.Management.System.entity.BookStatus.AVAILABLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;
    @Mock
    AuthorRepository authorRepository;
    @Mock
    PublisherRepository publisherRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    BookService bookService;
    private BookDto inputDto;
    private Book savedEntity;
    private BookDto reponse;
    private Author author;
    private Publisher publisher;
    private Long EXISTING_ID = 1030L;

    @BeforeEach
   public void setup()
   {
       inputDto = new BookDto(
               1030L, // ID is null for a new creation (even if 1030L was provided, we ignore it for a POST/save)
               "The Test Book Title",
               "ASDF45-9876543210", // Valid ISBN format
               LocalDate.of(2026, 1, 1),
               250,
               BigDecimal.valueOf(25.00), // Corrected to a reasonable price for a test
               AVAILABLE,
               "https://hf6536d/654",
               4545L,
               1212L,
               "Sunil Kumar", // Author ID
               "Raman Kumar"  // Publisher ID
       );

       savedEntity = new Book(
               1030L, // The Auto-Generated ID set by the repository mock
               inputDto.getTitle(),
               inputDto.getIsbn(),
               "This is a default description", // Assuming the service sets a default description
               inputDto.getPublicationDate(),
               inputDto.getPageCount(),
               inputDto.getPrice(),
               AVAILABLE,
               inputDto.getCoverImageUrl(),
               LocalDateTime.now(), // @PrePersist sets this
               LocalDateTime.now(), // @PrePersist sets this
               new Author(4545L),   // The Author object loaded via repository lookup
               new Publisher(1212L),// The Publisher object loaded via repository lookup
               null // No borrow records yet
       );

       Author mockAuthor = new Author(
               4545L, // ID is required
               "Sunil Kumar",
               "sunil@gmail.com", LocalDate.of(2004,10,15), "this is a small biography" // Fill in remaining fields if necessary for the test
       );

       Publisher mockPublisher = new Publisher(
               1212L, // ID is required
               "Raman Kumar",
               "raman@gmail.com", "GTB nagar", "4578964578", LocalDateTime.now(), null, null // Fill in remaining fields if necessary
       );
   }

   @Test
   public void saveBookTest()
   {
       when(modelMapper.map(any(BookDto.class), eq(Book.class)))
               .thenReturn(savedEntity);

       when(bookRepository.save(any(Book.class)))
               .thenReturn(savedEntity);

       when(modelMapper.map(any(Book.class), eq(BookDto.class)))
               .thenReturn(new BookDto(1030L, "The Test Book Title", null, null, null, null, null, null, 4545L, 1212L, null, null));

       when(authorRepository.findById(savedEntity.getAuthor().getId())).thenReturn(Optional.of(new Author(
               4545L, // ID is required
               "Sunil Kumar",
               "sunil@gmail.com", LocalDate.of(2004, 10, 15), "this is a small biography" // Fill in remaining fields if necessary for the test
       )));

       when(publisherRepository.findById(savedEntity.getPublisher().getId())).thenReturn(Optional.of(new Publisher(
               1212L, // ID is required
               "Raman Kumar",
               "raman@gmail.com", "GTB nagar", "4578964578", LocalDateTime.now(), null, null // Fill in remaining fields if necessary
       )));

       BookDto resultDto = bookService.saveBook(inputDto);

       assertNotNull(resultDto, "The result DTO should not be null.");
       assertEquals(1030L, resultDto.getId(), "The returned DTO must have the expected ID.");
       assertEquals(1030L,resultDto.getId());

       // 2. Verification: Prove the essential steps were executed
       verify(bookRepository, times(1)).save(any(Book.class));
       verify(modelMapper, times(2)).map(any(), any());
   }

   @Test
   @DisplayName("Should return BookDto when a book exists for the given ID")
   public void getBookByIdTest()
   {
        when(bookRepository.findById(EXISTING_ID)).thenReturn(Optional.of(savedEntity));
        when(modelMapper.map(any(Book.class),eq(BookDto.class))).thenReturn(inputDto);

        Optional<BookDto> dtoTest = bookService.getBookById(EXISTING_ID);

        assertTrue(dtoTest.isPresent());
        BookDto dto1 = dtoTest.get();
        assertEquals(EXISTING_ID,dto1.getId());
        assertEquals(inputDto.getTitle(),dto1.getTitle());


        verify(bookRepository , times(1)).findById(EXISTING_ID);
        verify(modelMapper, times(1)).map(any(),any());

   }

}