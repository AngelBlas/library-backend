package com.lekasoft.controller;

import com.lekasoft.dto.CategoryDTO;
import com.lekasoft.mapper.CategoryMapper;
import com.lekasoft.service.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {

    private final ICategoryService service;
    private final CategoryMapper mapper;

    @PostMapping
    public ResponseEntity<CategoryDTO> save(@Valid @RequestBody CategoryDTO categoryRequest) {
        var categoryDto = mapper.toCategoryDto(service.save(mapper.toCategory(categoryRequest)));
        return ResponseEntity.ok(categoryDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryRequest) {
        var categoryDto = mapper.toCategoryDto(service.update(id, mapper.toCategory(categoryRequest)));
        return ResponseEntity.ok(categoryDto);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> categoryDTOS = service.findAll().stream()
                .map(mapper::toCategoryDto)
                .toList();
        return ResponseEntity.ok(categoryDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        var categoryDto = mapper.toCategoryDto(service.findById(id));
        return ResponseEntity.ok(categoryDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
