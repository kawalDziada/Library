package com.example.library.book.dto;

import lombok.Data;

@Data
public class BookEntryDto {
    private Long id;
    private String name;
    private String author;
    private boolean isAvailable;
}
