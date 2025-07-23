ALTER TABLE books DROP FOREIGN KEY fk_books_author;
ALTER TABLE books DROP FOREIGN KEY fk_books_publisher;
ALTER TABLE borrow_record DROP FOREIGN KEY fk_borrow_user;
ALTER TABLE borrow_record DROP FOREIGN KEY fk_borrow_book;

ALTER TABLE books MODIFY COLUMN publisher_id BIGINT NULL;

ALTER TABLE books
  ADD CONSTRAINT fk_books_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE RESTRICT;

ALTER TABLE books
  ADD CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE SET NULL;

ALTER TABLE borrow_record
  ADD CONSTRAINT fk_borrow_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE borrow_record
  ADD CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE;
