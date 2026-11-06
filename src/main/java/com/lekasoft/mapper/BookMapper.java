package com.lekasoft.mapper;

import com.lekasoft.dto.BookDTO;
import com.lekasoft.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "category", ignore = true)
    Book toBook(BookDTO bookDTO);

    @Mapping(target = "category", source = "category.categoryId")
    BookDTO toBookDto(Book book);
}
