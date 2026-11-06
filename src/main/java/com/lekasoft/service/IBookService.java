package com.lekasoft.service;

import com.lekasoft.dto.BookDTO;
import com.lekasoft.model.Book;

public interface IBookService extends ICRUD<Book, Long> {

    Book saveFromDto(BookDTO bookDTO);

    Book updateFromDto(Long id, BookDTO bookDTO);
}
