package com.ehotel.hotel.controller;

import com.ehotel.hotel.model.Guest;
import com.ehotel.hotel.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/guests")
@RequiredArgsConstructor
public class GuestController {
    private final GuestRepository guestRepo;

    @GetMapping
    public List<Guest> getAll() {
        return guestRepo.findAll();
    }

    @PostMapping
    public Guest create(@RequestBody Guest guest) {
        return guestRepo.save(guest);
    }
}
