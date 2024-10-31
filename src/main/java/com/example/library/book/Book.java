package com.example.library.book;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
class Book {
    @Id
    @GeneratedValue
    private UUID id;

    private String isbn;
    private String name;
    private String author;
    private int pageNumber;
    private LocalDate publishDate;
    private boolean available = true;
    private UUID borrowedBy;

    public void markBorrowed(UUID userId) {
        this.available = false;
        this.borrowedBy = userId;
    }

    public void release() {
        this.available = true;
        this.borrowedBy = null;
    }

    public boolean isBorrowedBy(UUID userId) {
        return this.borrowedBy != null && this.borrowedBy.equals(userId);
    }
}
