package com.ehotel.hotel.repository;

import com.ehotel.hotel.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookingRepository extends JpaRepository<Booking, Long> {

}