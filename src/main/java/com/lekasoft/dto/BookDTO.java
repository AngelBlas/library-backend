package com.lekasoft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {
    private Long bookId;
    @NotBlank(message = "el título es obligatorio")
    @Size(max = 200, message = "el título no puede superar 200 caracteres")
    private String title;
    @NotBlank(message = "el autor es obligatorio")
    @Size(max = 150, message = "el autor no puede superar 150 caracteres")
    private String author;
    @Size(max = 150, message = "el isbn no puede superar 150 caracteres")
    private String isbn;
    @NotBlank(message = "la disponibilidad es obligatoria")
    private String available;
    @NotNull(message = "la categoría es obligatoria")
    private Long category;
}
