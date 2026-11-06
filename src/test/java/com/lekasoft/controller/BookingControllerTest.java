package com.lekasoft.controller;

import com.lekasoft.dto.BookingRequestDTO;
import com.lekasoft.dto.BookingResponseDTO;
import com.lekasoft.exception.NotFoundException;
import com.lekasoft.service.IBookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private IBookingService service;

    private BookingResponseDTO response() {
        return BookingResponseDTO.builder()
                .id(9L)
                .reservationDate(LocalDateTime.of(2026, 10, 9, 10, 30))
                .customerId(1L)
                .customerName("Ana Torres")
                .bookTitles(List.of("Clean Code", "Domain Driven Design"))
                .build();
    }

    @Test
    void registerReturnsCreatedWithBookingBody() throws Exception {
        when(service.register(any(BookingRequestDTO.class))).thenReturn(response());

        mockMvc.perform(post("/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":1,\"bookIds\":[10,20]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.customerName").value("Ana Torres"))
                .andExpect(jsonPath("$.bookTitles[0]").value("Clean Code"))
                .andExpect(jsonPath("$.bookTitles.length()").value(2));
    }

    @Test
    void registerWithoutBooksReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":1,\"bookIds\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("bookIds")));
    }

    @Test
    void registerWithoutCustomerReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookIds\":[10]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("customerId")));
    }

    @Test
    void registerWithUnknownCustomerReturnsNotFound() throws Exception {
        when(service.register(any(BookingRequestDTO.class)))
                .thenThrow(new NotFoundException("Cliente no encontrado: 77"));

        mockMvc.perform(post("/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":77,\"bookIds\":[10]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente no encontrado: 77"));
    }

    @Test
    void findAllReturnsReservationList() throws Exception {
        when(service.findAllReservations()).thenReturn(List.of(response()));

        mockMvc.perform(get("/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Ana Torres"))
                .andExpect(jsonPath("$[0].bookTitles[1]").value("Domain Driven Design"));
    }

    @Test
    void findByCustomerReturnsReservationsOfThatCustomer() throws Exception {
        when(service.findReservationsByCustomerId(1L)).thenReturn(List.of(response()));

        mockMvc.perform(get("/v1/bookings/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1));
    }
}
