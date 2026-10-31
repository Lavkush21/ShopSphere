package com.shopsphere.service;

import com.shopsphere.dto.AuthResponse;
import com.shopsphere.dto.LoginRequest;
import com.shopsphere.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}