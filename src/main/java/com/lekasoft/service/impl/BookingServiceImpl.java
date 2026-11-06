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
import com.lekasoft.repository.IGenericRepository;
import com.lekasoft.service.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl extends CRUDImpl<Booking, Long> implements IBookingService {

    private final IBookingRepository bookingRepository;
    private final ICustomerRepository customerRepository;
    private final IBookRepository bookRepository;
    private final BookingMapper mapper;

    @Override
    protected IGenericRepository<Booking, Long> getRepo() {
        return bookingRepository;
    }

    @Override
    @Transactional
    public BookingResponseDTO register(BookingRequestDTO request) {
        if (request.getBookIds() == null || request.getBookIds().isEmpty()) {
            throw new BadRequestException("Debe indicar al menos un libro");
        }
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado: " + request.getCustomerId()));
        List<Book> books = request.getBookIds().stream()
                .map(bookId -> bookRepository.findById(bookId)
                        .orElseThrow(() -> new NotFoundException("Libro no encontrado: " + bookId)))
                .toList();

        var booking = Booking.builder()
                .reservationDate(LocalDateTime.now())
                .customer(customer)
                .bookingDetails(new ArrayList<>())
                .build();
        books.forEach(book -> booking.getBookingDetails()
                .add(BookingDetail.builder().booking(booking).book(book).build()));

        return mapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> findAllReservations() {
        return bookingRepository.findAll().stream()
                .map(mapper::toBookingResponseDto)
                .distinct()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> findReservationsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerCustomerId(customerId).stream()
                .map(mapper::toBookingResponseDto)
                .distinct()
                .toList();
    }
}
