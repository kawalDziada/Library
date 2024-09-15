package com.example.library.book.dto;

import lombok.Value;
import java.time.LocalDate;

@Value
public class NewBookDto {
    String isbn;
    String name;
    String author;
    int pageNumber;
    LocalDate publishDate;
}
