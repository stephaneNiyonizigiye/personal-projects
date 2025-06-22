package com.eventaccess.eventaccess.security;


import com.eventaccess.eventaccess.Repository.AppUserRepository;
import com.eventaccess.eventaccess.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


import java.util.Collections;
import java.util.Optional;

    @Component
    @RequiredArgsConstructor
    public class AppUserDetailService implements UserDetailsService {

        private static final String DEFAULT_USER_ROLE = "ROLE_USER";

        private final AppUserRepository userRepo;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            Optional<AppUser> userRes = userRepo.findByUsername(username);
            if (userRes.isEmpty()) {
                throw new UsernameNotFoundException(String.format("User %s not found", username));
            }
            AppUser user = userRes.get();
            return new org.springframework.security.core.userdetails.User(
                    username,
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(DEFAULT_USER_ROLE))
            );
        }
    }



