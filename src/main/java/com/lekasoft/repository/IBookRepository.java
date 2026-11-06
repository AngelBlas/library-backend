package com.lekasoft.repository;

import com.lekasoft.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookRepository extends IGenericRepository<Book, Long> {
}
