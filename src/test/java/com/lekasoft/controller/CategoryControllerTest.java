package com.lekasoft.controller;

import com.lekasoft.dto.CategoryDTO;
import com.lekasoft.exception.NotFoundException;
import com.lekasoft.mapper.CategoryMapper;
import com.lekasoft.model.Category;
import com.lekasoft.service.ICategoryService;
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

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ICategoryService service;
    @MockitoBean
    private CategoryMapper mapper;

    private CategoryDTO categoryDto() {
        return CategoryDTO.builder()
                .categoryId(1L)
                .name("Programacion")
                .description("Libros de programacion")
                .status("ACTIVO")
                .build();
    }

    @Test
    void saveReturnsOkWithCategoryBody() throws Exception {
        Category category = Category.builder().categoryId(1L).name("Programacion").status("ACTIVO").build();
        when(mapper.toCategory(any(CategoryDTO.class))).thenReturn(category);
        when(service.save(category)).thenReturn(category);
        when(mapper.toCategoryDto(category)).thenReturn(categoryDto());

        mockMvc.perform(post("/v1/categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Programacion\",\"description\":\"Libros de programacion\",\"status\":\"ACTIVO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.name").value("Programacion"));
    }

    @Test
    void saveWithoutNameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/v1/categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"sin nombre\",\"status\":\"ACTIVO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("name")));
    }

    @Test
    void findByIdWithUnknownIdReturnsNotFound() throws Exception {
        when(service.findById(42L)).thenThrow(new NotFoundException("Registro no encontrado: 42"));

        mockMvc.perform(get("/v1/categories/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Registro no encontrado: 42"))
                .andExpect(jsonPath("$.path").value("/v1/categories/42"));
    }

    @Test
    void findAllReturnsCategoryList() throws Exception {
        Category category = Category.builder().categoryId(1L).name("Programacion").status("ACTIVO").build();
        when(service.findAll()).thenReturn(List.of(category));
        when(mapper.toCategoryDto(category)).thenReturn(categoryDto());

        mockMvc.perform(get("/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Programacion"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/v1/categories/1"))
                .andExpect(status().isNoContent());
    }
}
