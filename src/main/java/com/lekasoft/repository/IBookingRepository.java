package com.lekasoft.repository;

import com.lekasoft.model.Booking;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface IBookingRepository extends IGenericRepository<Booking, Long> {

    @Override
    @EntityGraph(attributePaths = {"customer", "bookingDetails.book"})
    List<Booking> findAll();

    @EntityGraph(attributePaths = {"customer", "bookingDetails.book"})
    List<Booking> findByCustomerCustomerId(Long customerId);
}
