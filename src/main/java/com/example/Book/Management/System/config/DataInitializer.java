package com.example.Book.Management.System.config;

import com.example.Book.Management.System.entity.*;
import com.example.Book.Management.System.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final PasswordEncoder passwordEncoder;
    private final BorrowRecordRepository borrowRecordRepository;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("user123");
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole(UserRole.USER);
            userRepository.save(user);

            User admin = new User();
            admin.setUsername("dipanshu_admin");
            admin.setFirstName("Dipanshu");
            admin.setLastName("Sehgal");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }

        Author author1 = null, author2 = null, author3 = null, author4 = null;
        if (authorRepository.count() == 0) {
            author1 = new Author("George R.R. Martin", "george.martin@example.com",
                    LocalDate.of(1948, 9, 20), "American novelist and short story writer");
            author2 = new Author("J.K. Rowling", "jk.rowling@example.com",
                    LocalDate.of(1965, 7, 31), "British author best known for Harry Potter");
            author3 = new Author("Neil Gaiman", "neil.gaiman@example.com",
                    LocalDate.of(1960, 11, 10), "Known for American Gods, Coraline");
            author4 = new Author("Brandon Sanderson", "brandon.sanderson@example.com",
                    LocalDate.of(1975, 12, 19), "Fantasy author of Mistborn & Stormlight Archive");
            authorRepository.saveAll(List.of(author1, author2, author3, author4));
        } else {
            List<Author> authors = authorRepository.findAll();
            author1 = authors.get(0);
            author2 = authors.get(1);
            author3 = authors.get(2);
            author4 = authors.get(3);
        }

        Publisher pub1 = null, pub2 = null, pub3 = null, pub4 = null;
        if (publisherRepository.count() == 0) {
            pub1 = new Publisher("Bantam Books");
            pub2 = new Publisher("Bloomsbury Publishing");
            pub3 = new Publisher("HarperCollins");
            pub4 = new Publisher("Tor Books");
            publisherRepository.saveAll(List.of(pub1, pub2, pub3, pub4));
        } else {
            List<Publisher> pubs = publisherRepository.findAll();
            pub1 = pubs.get(0);
            pub2 = pubs.get(1);
            pub3 = pubs.get(2);
            pub4 = pubs.get(3);
        }

        if (bookRepository.count() == 0) {
            List<Book> books = List.of(
                    new Book("A Game of Thrones", "9780553103540", "Epic fantasy series 'A Song of Ice and Fire'.",
                            LocalDate.of(1996, 8, 6), 694, new BigDecimal("29.99"), author1, pub1),

                    new Book("Harry Potter and the Philosopher's Stone", "9780747532699", "The first Harry Potter novel.",
                            LocalDate.of(1997, 6, 26), 223, new BigDecimal("19.99"), author2, pub2),

                    new Book("American Gods", "9780060558123", "Gaiman's Americana-mythic fantasy.",
                            LocalDate.of(2001, 6, 19), 465, new BigDecimal("24.50"), author3, pub3),

                    new Book("Mistborn: The Final Empire", "9780765311788", "Fantasy rebellion and Allomancy.",
                            LocalDate.of(2006, 7, 17), 541, new BigDecimal("22.75"), author4, pub4),

                    new Book("The Way of Kings", "9780765326355", "Stormlight Archive book one.",
                            LocalDate.of(2010, 8, 31), 1007, new BigDecimal("34.99"), author4, pub4),

                    new Book("A Clash of Kings", "9780553579901", "Second in 'A Song of Ice and Fire'.",
                            LocalDate.of(1998, 11, 16), 768, new BigDecimal("32.99"), author1, pub1),

                    new Book("A Storm of Swords", "9780553573428", "Third in 'A Song of Ice and Fire'.",
                            LocalDate.of(2000, 8, 8), 973, new BigDecimal("36.99"), author1, pub1),

                    new Book("Harry Potter and the Chamber of Secrets", "9780439064873", "Year 2 at Hogwarts.",
                            LocalDate.of(1998, 7, 2), 251, new BigDecimal("20.99"), author2, pub2),

                    new Book("Harry Potter and the Prisoner of Azkaban", "9780439136365", "Sirius Black enters.",
                            LocalDate.of(1999, 7, 8), 317, new BigDecimal("21.99"), author2, pub2),

                    new Book("Coraline", "9780380807345", "Dark fantasy for children.",
                            LocalDate.of(2002, 8, 4), 162, new BigDecimal("14.95"), author3, pub3),

                    new Book("Good Omens", "9780060853983", "Armageddon comedy with Terry Pratchett.",
                            LocalDate.of(1990, 5, 1), 288, new BigDecimal("19.95"), author3, pub3),

                    new Book("Elantris", "9780765350374", "Sanderson's debut novel.",
                            LocalDate.of(2005, 4, 21), 638, new BigDecimal("24.00"), author4, pub4),

                    new Book("Warbreaker", "9780765320308", "Standalone fantasy with biochromatic magic.",
                            LocalDate.of(2009, 6, 9), 688, new BigDecimal("28.00"), author4, pub4),

                    new Book("A Feast for Crows", "9780553582024", "Fourth in 'A Song of Ice and Fire'.",
                            LocalDate.of(2005, 11, 8), 753, new BigDecimal("35.00"), author1, pub1),

                    new Book("A Dance with Dragons", "9780553582017", "Fifth in the fantasy series.",
                            LocalDate.of(2011, 7, 12), 1016, new BigDecimal("38.00"), author1, pub1)
            );

            books.forEach(book -> book.setStatus(BookStatus.AVAILABLE));
            bookRepository.saveAll(books);
        }

        if (borrowRecordRepository.count() == 0) {
            User user = userRepository.findByEmail("user@example.com").orElseThrow();
            Book book1 = bookRepository.findByTitle("A Game of Thrones").orElseThrow();
            Book book2 = bookRepository.findByTitle("Harry Potter and the Philosopher's Stone").orElseThrow();

            BorrowRecord r1 = new BorrowRecord(user, book1, LocalDate.now().minusDays(3), null, BorrowStatus.BORROWED);
            BorrowRecord r2 = new BorrowRecord(user, book2, LocalDate.now().minusDays(10), LocalDate.now(), BorrowStatus.RETURNED);

            book1.setStatus(BookStatus.BORROWED);
            book2.setStatus(BookStatus.AVAILABLE);

            bookRepository.saveAll(List.of(book1, book2));
            borrowRecordRepository.saveAll(List.of(r1, r2));
        }
    }
}
