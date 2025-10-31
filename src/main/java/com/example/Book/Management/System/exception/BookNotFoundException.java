package com.example.Book.Management.System.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.NOT_FOUND,
        reason = "Book Not Found in Database"
)
public class BookNotFoundException extends RuntimeException{

    public BookNotFoundException(Long id)
    {
        super("book not found with id- " +id);
    }
}
