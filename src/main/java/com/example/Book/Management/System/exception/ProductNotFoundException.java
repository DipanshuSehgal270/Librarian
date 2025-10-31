package com.example.Book.Management.System.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    value = HttpStatus.NOT_FOUND,
    reason = "The specified product could not be located in the system."
)
public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(Long productId) {
        super("Product with ID " + productId + " was not found.");
    }

    public ProductNotFoundException()
    {
        super();
    }
}
