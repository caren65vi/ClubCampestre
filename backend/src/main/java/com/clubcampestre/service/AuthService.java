package com.clubcampestre.service;

import com.clubcampestre.dto.LoginRequest;
import com.clubcampestre.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
