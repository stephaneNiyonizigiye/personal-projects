package com.eventaccess.eventaccess.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @RequestMapping("/")
    public String getAllEvents(){
        return "Welcome to All Events";
    }
    @RequestMapping("/myEvents")
    public String getMyEvents(){
        return "My Events";
    }
}
