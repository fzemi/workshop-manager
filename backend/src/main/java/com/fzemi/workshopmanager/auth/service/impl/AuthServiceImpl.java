package com.fzemi.workshopmanager.auth.service.impl;

import com.fzemi.workshopmanager.auth.dto.LoginRequest;
import com.fzemi.workshopmanager.auth.dto.LoginResponse;
import com.fzemi.workshopmanager.auth.service.AuthService;
import com.fzemi.workshopmanager.security.service.impl.JwtService;
import com.fzemi.workshopmanager.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = ((User) auth.getPrincipal());
        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .build();
    }
}
