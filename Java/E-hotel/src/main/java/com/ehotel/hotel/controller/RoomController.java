package com.ehotel.hotel.controller;

import com.ehotel.hotel.model.Room;
import com.ehotel.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomRepository roomRepo;

    @GetMapping
    public List<Room> getAll() {
        return roomRepo.findAll();
    }

    @PostMapping
    public Room create(@RequestBody Room room) {
        return roomRepo.save(room);
    }
}
