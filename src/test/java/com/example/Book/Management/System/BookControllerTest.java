package com.example.Book.Management.System;

import com.example.Book.Management.System.controller.BookController;
import com.example.Book.Management.System.dto.BookDto;
import com.example.Book.Management.System.security.config.SecurityConfig;
import com.example.Book.Management.System.security.jwt.JwtAuthenticationFilter;
import com.example.Book.Management.System.security.jwt.JwtUtils;
import com.example.Book.Management.System.service.BookService;
import com.example.Book.Management.System.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void testGetBooksWithPagination() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");

        Page<BookDto> page = new PageImpl<>(List.of(bookDto));
        Mockito.when(bookService.getAllBooks(0, 5, "title")).thenReturn(page);

        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Book"));
    }
}
