package com.example.library.book;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

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

    public void markBorrowed(UUID userId) {
        this.isAvailable = false;
        this.borrowedBy = userId;
    }

    public void release() {
        this.isAvailable = true;
        this.borrowedBy = null;
    }
}