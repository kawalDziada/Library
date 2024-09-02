package com.example.library.book.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class BookDto {
    private Long id;
    private String isbn;
    private String name;
    private String author;
    private int pageNumber;
    private LocalDate publishDate;
    private boolean isAvailable;
    private UUID borrowedBy;
}
