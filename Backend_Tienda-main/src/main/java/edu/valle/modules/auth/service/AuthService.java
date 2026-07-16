package edu.valle.modules.auth.service;

import edu.valle.modules.auth.dto.request.LoginRequest;
import edu.valle.modules.auth.dto.request.RegisterRequest;
import edu.valle.modules.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
