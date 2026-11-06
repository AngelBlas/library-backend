package com.lekasoft.controller;

import com.lekasoft.dto.BookDTO;
import com.lekasoft.mapper.BookMapper;
import com.lekasoft.service.IBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/books")
public class BookController {

    private final IBookService service;
    private final BookMapper mapper;

    @PostMapping
    public ResponseEntity<BookDTO> save(@Valid @RequestBody BookDTO bookRequest) {
        var bookDto = mapper.toBookDto(service.saveFromDto(bookRequest));
        return ResponseEntity.ok(bookDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> update(@PathVariable Long id, @Valid @RequestBody BookDTO bookRequest) {
        var bookDto = mapper.toBookDto(service.updateFromDto(id, bookRequest));
        return ResponseEntity.ok(bookDto);
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> findAll() {
        List<BookDTO> bookDTOS = service.findAll().stream()
                .map(mapper::toBookDto)
                .toList();
        return ResponseEntity.ok(bookDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> findById(@PathVariable Long id) {
        var bookDto = mapper.toBookDto(service.findById(id));
        return ResponseEntity.ok(bookDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
