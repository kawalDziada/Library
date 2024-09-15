package com.example.library.book.dto;

import lombok.Value;

@Value
public class BookEntryDto {
    Long id;
    String name;
    String author;
    boolean isAvailable;
}
