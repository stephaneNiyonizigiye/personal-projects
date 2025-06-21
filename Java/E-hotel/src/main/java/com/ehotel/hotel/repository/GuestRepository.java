package com.ehotel.hotel.repository;
import com.ehotel.hotel.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GuestRepository extends JpaRepository<Guest, Long> {
}
