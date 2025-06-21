package com.ehotel.hotel.controller;
import com.ehotel.hotel.model.Booking;
import com.ehotel.hotel.model.Guest;
import com.ehotel.hotel.model.Room;
import com.ehotel.hotel.repository.BookingRepository;
import com.ehotel.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingRepository bookingRepo;
    private final RoomRepository roomRepo;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Booking booking) {
        Room room = booking.getRoom();
        Guest guest = booking.getGuest();
        if (guest == null) {
            return ResponseEntity.badRequest().body("Guest not found");
        }
        else if (room == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }
        else if (!room.getAvailable()) {
            return ResponseEntity.badRequest().body("Room not available");
        }
        room.setAvailable(false);
        return ResponseEntity.ok(bookingRepo.save(booking));
    }

    @GetMapping
    public List<Booking> getAll() {
        return bookingRepo.findAll();
    }
}
