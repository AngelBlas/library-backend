package com.lekasoft.service.impl;

import com.lekasoft.dto.BookDTO;
import com.lekasoft.exception.NotFoundException;
import com.lekasoft.mapper.BookMapper;
import com.lekasoft.model.Book;
import com.lekasoft.model.Category;
import com.lekasoft.repository.IBookRepository;
import com.lekasoft.repository.ICategoryRepository;
import com.lekasoft.repository.IGenericRepository;
import com.lekasoft.service.IBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl extends CRUDImpl<Book, Long> implements IBookService {

    private final IBookRepository repository;
    private final ICategoryRepository categoryRepository;
    private final BookMapper mapper;

    @Override
    protected IGenericRepository<Book, Long> getRepo() {
        return repository;
    }

    @Override
    public Book saveFromDto(BookDTO bookDTO) {
        Book book = mapper.toBook(bookDTO);
        book.setCategory(resolveCategory(bookDTO.getCategory()));
        log.info("Registrando libro: {}", book.getTitle());
        return repository.save(book);
    }

    @Override
    public Book updateFromDto(Long id, BookDTO bookDTO) {
        Book existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Libro no encontrado: " + id));
        Book book = mapper.toBook(bookDTO);
        book.setBookId(existing.getBookId());
        book.setCategory(resolveCategory(bookDTO.getCategory()));
        return repository.save(book);
    }

    private Category resolveCategory(Long categoryName) {
        return categoryRepository.findById(categoryName)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + categoryName));
    }
}
