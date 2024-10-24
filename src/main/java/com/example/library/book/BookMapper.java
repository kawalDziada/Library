package com.example.library.book;

import com.example.library.book.dto.NewBookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAvailable", constant = "true")
    @Mapping(target = "borrowedBy", ignore = true)
    Book toBook(NewBookDto newBookDto);
}
