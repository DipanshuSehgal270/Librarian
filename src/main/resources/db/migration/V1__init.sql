-- USERS TABLE
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
);

-- PUBLISHERS TABLE
CREATE TABLE publishers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    address VARCHAR(200),
    phone_number VARCHAR(20),
    created_at DATETIME,
    updated_at DATETIME
);

-- AUTHORS TABLE
CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    birth_date DATE,
    biography VARCHAR(500),
    created_at DATETIME,
    updated_at DATETIME
);

-- BOOKS TABLE
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(17) NOT NULL UNIQUE,
    description VARCHAR(1000),
    publication_date DATE,
    page_count INT,
    price DECIMAL(10, 2),
    status VARCHAR(20) NOT NULL,
    cover_image_url VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    author_id BIGINT NOT NULL,
    publisher_id BIGINT NOT NULL,
    CONSTRAINT fk_books_author FOREIGN KEY (author_id) REFERENCES authors(id),
    CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id)
);

-- BORROW_RECORD TABLE
CREATE TABLE borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    book_id BIGINT,
    borrow_date DATE,
    return_date DATE,
    status VARCHAR(20),
    CONSTRAINT fk_borrow_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES books(id)
);
