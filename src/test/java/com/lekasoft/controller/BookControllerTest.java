package com.lekasoft.controller;

import com.lekasoft.dto.BookDTO;
import com.lekasoft.exception.NotFoundException;
import com.lekasoft.mapper.BookMapper;
import com.lekasoft.model.Book;
import com.lekasoft.service.IBookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private IBookService service;
    @MockitoBean
    private BookMapper mapper;

    private BookDTO bookDto() {
        return BookDTO.builder()
                .bookId(1L)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .available("SI")
                .category("Programacion")
                .build();
    }

    private String validJson() {
        return """
                {
                  "title": "Clean Code",
                  "author": "Robert C. Martin",
                  "isbn": "9780132350884",
                  "available": "SI",
                  "category": "Programacion"
                }
                """;
    }

    @Test
    void saveReturnsOkWithBookBody() throws Exception {
        Book book = Book.builder().bookId(1L).title("Clean Code").build();
        when(service.saveFromDto(any(BookDTO.class))).thenReturn(book);
        when(mapper.toBookDto(book)).thenReturn(bookDto());

        mockMvc.perform(post("/v1/books").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.category").value("Programacion"));
    }

    @Test
    void saveWithBlankFieldsReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/v1/books").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"author\":\"\",\"available\":\"\",\"category\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("title")))
                .andExpect(jsonPath("$.path").value("/v1/books"));
    }

    @Test
    void saveWithUnknownCategoryReturnsNotFound() throws Exception {
        when(service.saveFromDto(any(BookDTO.class)))
                .thenThrow(new NotFoundException("Categoría no encontrada: Nope"));

        mockMvc.perform(post("/v1/books").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Categoría no encontrada: Nope"));
    }

    @Test
    void findAllReturnsBookList() throws Exception {
        Book book = Book.builder().bookId(1L).title("Clean Code").build();
        when(service.findAll()).thenReturn(List.of(book));
        when(mapper.toBookDto(book)).thenReturn(bookDto());

        mockMvc.perform(get("/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void findByIdWithUnknownIdReturnsNotFound() throws Exception {
        when(service.findById(99L)).thenThrow(new NotFoundException("Registro no encontrado: 99"));

        mockMvc.perform(get("/v1/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Registro no encontrado: 99"))
                .andExpect(jsonPath("$.path").value("/v1/books/99"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/v1/books/1"))
                .andExpect(status().isNoContent());
    }
}
