package com.lekasoft.controller;

import com.lekasoft.dto.BookingRequestDTO;
import com.lekasoft.dto.BookingResponseDTO;
import com.lekasoft.service.IBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookings")
public class BookingController {

    private final IBookingService service;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> register(@Valid @RequestBody BookingRequestDTO bookingRequest) {
        var bookingDto = service.register(bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingDto);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAllReservations());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingResponseDTO>> findByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.findReservationsByCustomerId(customerId));
    }
}
