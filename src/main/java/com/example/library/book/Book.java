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
import com.example.library.book.dto.NewBookDto;

@Entity
@Data
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
class Book {
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

    public static Book fromDto(NewBookDto newBookDto) {
        return BookMapper.INSTANCE.toBook(newBookDto);
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
