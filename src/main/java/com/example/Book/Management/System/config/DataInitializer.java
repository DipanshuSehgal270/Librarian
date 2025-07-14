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

        // 🧑 Create users
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("user123");
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole(UserRole.USER);
            userRepository.save(user); // ✅ You missed saving this!

            User admin = new User();
            admin.setUsername("dipanshu_admin");
            admin.setFirstName("Dipanshu");
            admin.setLastName("Sehgal");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }

        // 🧑‍🎓 Create authors
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

        // 🏢 Create publishers
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

        // 📚 Create books
        if (bookRepository.count() == 0) {
            Book book1 = new Book(
                    "A Game of Thrones", "9780553103540",
                    "The first book in George R.R. Martin's epic fantasy series 'A Song of Ice and Fire'.",
                    LocalDate.of(1996, 8, 6), 694,
                    new BigDecimal("29.99"), author1, pub1
            );
            Book book2 = new Book(
                    "Harry Potter and the Philosopher's Stone", "9780747532699",
                    "The first novel in the Harry Potter series, introducing the young wizard Harry Potter.",
                    LocalDate.of(1997, 6, 26), 223,
                    new BigDecimal("19.99"), author2, pub2
            );
            Book book3 = new Book(
                    "American Gods", "9780060558123",
                    "Neil Gaiman's novel blending Americana, fantasy, and mythology.",
                    LocalDate.of(2001, 6, 19), 465,
                    new BigDecimal("24.50"), author3, pub3
            );
            Book book4 = new Book(
                    "Mistborn: The Final Empire", "9780765311788",
                    "Brandon Sanderson's high fantasy novel featuring Allomancy and rebellion.",
                    LocalDate.of(2006, 7, 17), 541,
                    new BigDecimal("22.75"), author4, pub4
            );
            Book book5 = new Book(
                    "The Way of Kings", "9780765326355",
                    "The first book of The Stormlight Archive, Sanderson's epic fantasy masterpiece.",
                    LocalDate.of(2010, 8, 31), 1007,
                    new BigDecimal("34.99"), author4, pub4
            );

            // Set initial status
            List<Book> books = List.of(book1, book2, book3, book4, book5);
            books.forEach(b -> b.setStatus(BookStatus.AVAILABLE));
            bookRepository.saveAll(books);
        }

        // 📕 Create sample borrow records
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
