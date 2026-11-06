package com.lekasoft.dto;

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
public class BookingResponseDTO {
    private Long id;
    private LocalDateTime reservationDate;
    private Long customerId;
    private String customerName;
    private List<String> bookTitles;
}
