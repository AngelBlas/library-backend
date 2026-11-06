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
public class CustomerDTO {
    private Long customerId;
    @NotBlank(message = "el nombre es obligatorio")
    @Size(max = 200)
    private String firstName;
    @NotBlank(message = "el apellido es obligatorio")
    @Size(max = 200)
    private String lastName;
    @NotBlank(message = "el numero de card es obligatorio")
    @Size(max = 10)
    private String cardId;
    @NotBlank(message = "el email es obligatorio")
    @Size(max = 200)
    private String email;
}
