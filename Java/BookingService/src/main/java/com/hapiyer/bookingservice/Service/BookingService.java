package com.hapiyer.bookingservice.Service;

import com.hapiyer.bookingservice.dto.BookingDto;
import com.hapiyer.bookingservice.dto.UpdateBookingDto;
import com.hapiyer.bookingservice.model.BookingModel;
import com.hapiyer.bookingservice.repository.BookingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepo bookingRepo;
    

    public BookingModel saveBooking(BookingDto bookingDto) {
        BookingModel bookingModel = BookingModel.builder()
                .email(bookingDto.getEmail())
                .firstName(bookingDto.getFirstName())
                .lastName(bookingDto.getLastName())
                .active(true)
                .role("Client")
                .build();
        bookingRepo.save(bookingModel);
        return bookingModel;
    }

    public void deleteBooking(String id) {
        bookingRepo.deleteById(id);
    }
    
    public BookingModel updateBooking(UpdateBookingDto updateBookingDto) {
        BookingModel bookingModel = bookingRepo.findById(updateBookingDto.getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        bookingModel.setFirstName(updateBookingDto.getFirstName());
        bookingModel.setLastName(updateBookingDto.getLastName());
        bookingModel.setActive(updateBookingDto.isActive());
        bookingRepo.save(bookingModel);
        return bookingModel;
    }

    public BookingModel getBooking(String id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking with id: " + id + " not found"
                ));
    }
    public Iterable<BookingModel> getAllBookings() {
        return bookingRepo.findAll();
    }
}
