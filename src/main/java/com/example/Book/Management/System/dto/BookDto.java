package com.example.Book.Management.System.dto;

import com.example.Book.Management.System.entity.BookStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


public class BookDto {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200)
    private String title;

    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 17)
    private String isbn;

    private String description;
    private LocalDate publicationDate;

    @Min(value = 1, message = "Page count must be at least 1")
    private Integer pageCount;

    private BigDecimal price;
    private BookStatus status;
    private String coverImageUrl;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotNull(message = "Publisher ID is required")
    private Long publisherId;

    private String authorName;
    private String publisherName;

    // Constructors
    public BookDto() {}


    public BookDto(String title, String isbn, LocalDate publicationDate, Integer pageCount, BigDecimal price, BookStatus status, String coverImageUrl, Long authorId, Long publisherId, String authorName, String publisherName) {
        this.title = title;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.pageCount = pageCount;
        this.price = price;
        this.status = status;
        this.coverImageUrl = coverImageUrl;
        this.authorId = authorId;
        this.publisherId = publisherId;
        this.authorName = authorName;
        this.publisherName = publisherName;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Long getPublisherId() { return publisherId; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
}
