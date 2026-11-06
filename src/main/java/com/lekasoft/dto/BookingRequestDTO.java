package com.lekasoft.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequestDTO {
    @NotNull(message = "el id del cliente es obligatorio")
    private Long customerId;
    @NotEmpty(message = "debe indicar al menos un libro")
    private List<Long> bookIds;
    @NotNull(message =  "el campo fecha de reserva no debe ser null")
    private LocalDateTime dateReservation;
}
