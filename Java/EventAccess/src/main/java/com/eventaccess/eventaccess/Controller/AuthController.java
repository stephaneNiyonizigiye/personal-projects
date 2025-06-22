package com.eventaccess.eventaccess.Controller;

import com.eventaccess.eventaccess.Repository.AppUserRepository;
import com.eventaccess.eventaccess.model.AppUser;
import com.eventaccess.eventaccess.model.LoginCreds;
import com.eventaccess.eventaccess.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserRepository userRepo;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final PasswordEncoder encoder;

    @PostMapping("/register")
    public Map<String, Object> registerHandler(@RequestBody AppUser user) {
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepo.save(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return Collections.singletonMap("jwt-token",token);
    }
    @PostMapping("/login")
    public Map<String,Object> loginHandler(@RequestBody LoginCreds body){

        try {
            UsernamePasswordAuthenticationToken authInputToken=
                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword());
            authManager.authenticate(authInputToken);

            String token=jwtUtil.generateToken(body.getUsername());
            return Collections.singletonMap("jwt-token",token);
        }catch (AuthenticationException authExec){
            throw new RuntimeException("Invalid username/password supplied");
        }
    }
}
