package com.hapiyer.bookingservice.repository;

import com.hapiyer.bookingservice.model.BookingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepo extends JpaRepository<BookingModel, String> {
}
