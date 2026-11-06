package com.lekasoft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
    private Long categoryId;
    @NotBlank(message = "el nombre es obligatorio")
    @Size(max = 100, message = "el nombre no puede superar 100 caracteres")
    private String name;
    @Size(max = 500, message = "la descripción no puede superar 500 caracteres")
    private String description;
    @NotBlank(message = "el estado es obligatorio")
    private String status;
}
