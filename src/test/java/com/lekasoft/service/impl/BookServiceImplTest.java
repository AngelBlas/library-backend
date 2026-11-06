package com.lekasoft.service.impl;

import com.lekasoft.dto.BookDTO;
import com.lekasoft.exception.NotFoundException;
import com.lekasoft.mapper.BookMapper;
import com.lekasoft.model.Book;
import com.lekasoft.model.Category;
import com.lekasoft.repository.IBookRepository;
import com.lekasoft.repository.ICategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private IBookRepository repository;
    @Mock
    private ICategoryRepository categoryRepository;
    @Mock
    private BookMapper mapper;
    @InjectMocks
    private BookServiceImpl service;

    private BookDTO dto() {
        return BookDTO.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .available("SI")
                .category("Programacion")
                .build();
    }

    private Category category() {
        return Category.builder().categoryId(3L).name("Programacion").status("ACTIVO").build();
    }

    @Test
    void saveFromDtoResolvesCategoryByName() {
        Book mapped = Book.builder().title("Clean Code").author("Robert C. Martin").available("SI").build();
        Category category = category();

        when(mapper.toBook(dto())).thenReturn(mapped);
        when(categoryRepository.findByName("Programacion")).thenReturn(Optional.of(category));
        when(repository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.saveFromDto(dto());

        assertThat(result.getCategory()).isSameAs(category);
        verify(repository).save(mapped);
    }

    @Test
    void saveFromDtoWithUnknownCategoryReturnsNotFound() {
        when(mapper.toBook(dto())).thenReturn(Book.builder().build());
        when(categoryRepository.findByName("Programacion")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.saveFromDto(dto()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Categoría no encontrada");
        verify(repository, never()).save(any(Book.class));
    }

    @Test
    void updateFromDtoWithUnknownBookReturnsNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateFromDto(99L, dto()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Libro no encontrado");
        verify(repository, never()).save(any(Book.class));
    }

    @Test
    void updateFromDtoKeepsExistingIdAndResolvesCategory() {
        Book existing = Book.builder().bookId(5L).title("Antiguo").build();
        Book mapped = Book.builder().title("Clean Code").author("Robert C. Martin").available("SI").build();
        Category category = category();

        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(mapper.toBook(dto())).thenReturn(mapped);
        when(categoryRepository.findByName("Programacion")).thenReturn(Optional.of(category));
        when(repository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.updateFromDto(5L, dto());

        assertThat(result.getBookId()).isEqualTo(5L);
        assertThat(result.getCategory()).isSameAs(category);
    }
}
