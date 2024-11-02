package com.example.library.book;

import com.example.library.book.dto.NewBookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "available", constant = "true")
    @Mapping(target = "borrowedBy", ignore = true)
    Book fromNewBookDto(NewBookDto newBookDto);
}
