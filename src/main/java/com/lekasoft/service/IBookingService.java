package com.lekasoft.service;

import com.lekasoft.dto.BookingRequestDTO;
import com.lekasoft.dto.BookingResponseDTO;
import com.lekasoft.model.Booking;

import java.util.List;

public interface IBookingService extends ICRUD<Booking, Long> {

    BookingResponseDTO register(BookingRequestDTO request);

    List<BookingResponseDTO> findAllReservations();

    List<BookingResponseDTO> findReservationsByCustomerId(Long customerId);
}
