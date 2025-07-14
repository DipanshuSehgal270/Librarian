package com.example.Book.Management.System;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class healthCheck {

    @GetMapping("/healthCheack")
    public static void healthCheck()
    {
        System.out.println("This is health check controller.");
    }
}
