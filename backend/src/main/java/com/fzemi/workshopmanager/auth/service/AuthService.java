package com.fzemi.workshopmanager.auth.service;

import com.fzemi.workshopmanager.auth.dto.LoginRequest;
import com.fzemi.workshopmanager.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
