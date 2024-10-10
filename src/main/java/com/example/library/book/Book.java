package com.example.library.book;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;
import com.example.library.book.dto.NewBookDto;

@Entity
@Data
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String isbn;
    private String name;
    private String author;
    private int pageNumber;
    private LocalDate publishDate;
    private boolean isAvailable = true;
    private UUID borrowedBy;

    protected Book() {
    }

    public Book(Long id, String isbn, String name, String author, int pageNumber, LocalDate publishDate, boolean isAvailable) {
        this.id = id;
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.pageNumber = pageNumber;
        this.publishDate = publishDate;
        this.isAvailable = isAvailable;
    }

    public static Book ofNew(NewBookDto newBookDto) {
        return new Book(
                null,
                newBookDto.getIsbn(),
                newBookDto.getName(),
                newBookDto.getAuthor(),
                newBookDto.getPageNumber(),
                newBookDto.getPublishDate(),
                true
        );
    }

    public void markBorrowed(UUID userId) {
        this.isAvailable = false;
        this.borrowedBy = userId;
    }

    public void release() {
        this.isAvailable = true;
        this.borrowedBy = null;
    }

    public boolean isBorrowedBy(UUID userId) {
        return this.borrowedBy != null && this.borrowedBy.equals(userId);
    }
}