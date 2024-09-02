package com.example.library.book.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NewBookDto {
    private String isbn;
    private String name;
    private String author;
    private int pageNumber;
    private LocalDate publishDate;
}
