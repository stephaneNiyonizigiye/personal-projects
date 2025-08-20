package com.hapiyer.bookingservice.controller;

import com.hapiyer.bookingservice.Service.BookingService;
import com.hapiyer.bookingservice.dto.BookingDto;
import com.hapiyer.bookingservice.dto.UpdateBookingDto;
import com.hapiyer.bookingservice.model.BookingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/getAll")
    public ResponseEntity<Iterable<BookingModel>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingModel> getBookingById(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable String id) {
        bookingService.deleteBooking(id);
    }

    @PostMapping("/create")
    public ResponseEntity<BookingModel> createBooking(@RequestBody BookingDto bookingDto) {
        BookingModel bookingModel = bookingService.saveBooking(bookingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingModel);
    }
    @PutMapping("/update")
    public ResponseEntity<BookingModel> updateBooking(@RequestBody UpdateBookingDto updateBookingDto){
        BookingModel bookingModel= bookingService.updateBooking(updateBookingDto);
        return ResponseEntity.ok(bookingModel);
    }
}

