package com.eventaccess.eventaccess.Controller;


import com.eventaccess.eventaccess.Repository.AppUserRepository;
import com.eventaccess.eventaccess.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AppUserRepository userRepo;

    @RequestMapping("/info")
    public AppUser getUserDetails() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findByUsername(username).get();
        }

    }


