package com.hapiyer.bookingservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BookingDto {
    private final String firstName;
    private final String lastName;
    private final String email;
}
