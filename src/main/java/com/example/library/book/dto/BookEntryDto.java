package com.example.library.book.dto;

import lombok.Value;
import java.util.UUID;

@Value
public class BookEntryDto {
    UUID id;
    String name;
    String author;
    boolean isAvailable;
}
