package com.example.library.book.dto;

import lombok.Value;
import java.time.LocalDate;
import java.util.UUID;

@Value
public class BookDto {
    UUID id;
    String isbn;
    String name;
    String author;
    int pageNumber;
    LocalDate publishDate;
    boolean isAvailable;
    UUID borrowedBy;
}
