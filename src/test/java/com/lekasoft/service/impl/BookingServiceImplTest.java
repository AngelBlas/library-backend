package com.lekasoft.service.impl;

import com.lekasoft.dto.BookingRequestDTO;
import com.lekasoft.dto.BookingResponseDTO;
import com.lekasoft.exception.BadRequestException;
import com.lekasoft.exception.NotFoundException;
import com.lekasoft.mapper.BookingMapper;
import com.lekasoft.model.Book;
import com.lekasoft.model.Booking;
import com.lekasoft.model.BookingDetail;
import com.lekasoft.model.Customer;
import com.lekasoft.repository.IBookRepository;
import com.lekasoft.repository.IBookingRepository;
import com.lekasoft.repository.ICustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private IBookingRepository bookingRepository;
    @Mock
    private ICustomerRepository customerRepository;
    @Mock
    private IBookRepository bookRepository;
    @Mock
    private BookingMapper mapper;
    @InjectMocks
    private BookingServiceImpl service;

    private Customer customer() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setFirstName("Ana");
        customer.setLastName("Torres");
        customer.setEmail("ana@mail.com");
        return customer;
    }

    @Test
    void registerCreatesBookingWithOneDetailPerBook() {
        Customer customer = customer();
        Book book1 = Book.builder().bookId(10L).title("Clean Code").build();
        Book book2 = Book.builder().bookId(20L).title("Domain Driven Design").build();
        BookingResponseDTO response = BookingResponseDTO.builder().id(99L).build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(20L)).thenReturn(Optional.of(book2));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toBookingResponseDto(any(Booking.class))).thenReturn(response);

        var request = BookingRequestDTO.builder().customerId(1L).bookIds(List.of(10L, 20L)).dateReservation(LocalDateTime.now()).build();
        var result = service.register(request);

        assertThat(result).isSameAs(response);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking saved = captor.getValue();
        assertThat(saved.getCustomer()).isSameAs(customer);
        assertThat(saved.getReservationDate()).isNotNull();
        assertThat(saved.getBookingDetails()).hasSize(2);
        assertThat(saved.getBookingDetails())
                .allSatisfy(detail -> assertThat(detail.getBooking()).isSameAs(saved));
        assertThat(saved.getBookingDetails())
                .extracting(BookingDetail::getBook)
                .containsExactlyInAnyOrder(book1, book2);
    }

    @Test
    void registerWithUnknownCustomerReturnsNotFound() {
        when(customerRepository.findById(77L)).thenReturn(Optional.empty());

        var request = BookingRequestDTO.builder().customerId(77L).bookIds(List.of(10L)).build();

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void registerWithUnknownBookReturnsNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer()));
        when(bookRepository.findById(77L)).thenReturn(Optional.empty());

        var request = BookingRequestDTO.builder().customerId(1L).bookIds(List.of(77L)).build();

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Libro no encontrado");
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void registerWithEmptyBookListReturnsBadRequest() {
        var request = BookingRequestDTO.builder().customerId(1L).bookIds(List.of()).build();

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("al menos un libro");
        verifyNoInteractions(customerRepository, bookRepository, bookingRepository);
    }

    @Test
    void findAllReservationsReturnsMappedBookings() {
        Book book = Book.builder().bookId(10L).title("Clean Code").build();
        Booking booking = Booking.builder()
                .id(5L)
                .customer(customer())
                .bookingDetails(new ArrayList<>(List.of(BookingDetail.builder().book(book).build())))
                .build();
        BookingResponseDTO response = BookingResponseDTO.builder().id(5L).build();

        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        when(mapper.toBookingResponseDto(booking)).thenReturn(response);

        var result = service.findAllReservations();

        assertThat(result).containsExactly(response);
    }

    @Test
    void findReservationsByCustomerIdReturnsMappedBookings() {
        Book book = Book.builder().bookId(10L).title("Clean Code").build();
        Booking booking = Booking.builder()
                .id(6L)
                .customer(customer())
                .bookingDetails(new ArrayList<>(List.of(BookingDetail.builder().book(book).build())))
                .build();
        BookingResponseDTO response = BookingResponseDTO.builder().id(6L).build();

        when(bookingRepository.findByCustomerCustomerId(1L)).thenReturn(List.of(booking));
        when(mapper.toBookingResponseDto(booking)).thenReturn(response);

        var result = service.findReservationsByCustomerId(1L);

        assertThat(result).containsExactly(response);
    }
}
