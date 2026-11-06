package com.lekasoft.mapper;

import com.lekasoft.dto.BookingResponseDTO;
import com.lekasoft.model.Booking;
import com.lekasoft.model.BookingDetail;
import com.lekasoft.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "customerId", source = "customer.customerId")
    @Mapping(target = "customerName", source = "customer", qualifiedByName = "customerName")
    @Mapping(target = "bookTitles", source = "bookingDetails", qualifiedByName = "bookTitles")
    BookingResponseDTO toBookingResponseDto(Booking booking);

    @Named("customerName")
    default String customerName(Customer customer) {
        return customer.getFirstName() + " " + customer.getLastName();
    }

    @Named("bookTitles")
    default List<String> bookTitles(List<BookingDetail> bookingDetails) {
        return bookingDetails.stream()
                .map(detail -> detail.getBook().getTitle())
                .toList();
    }
}
