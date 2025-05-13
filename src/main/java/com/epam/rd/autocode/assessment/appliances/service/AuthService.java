package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.dto.LoginUserDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientCreateDTO;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.Authentication;

public interface AuthService {
    boolean isValidSetupPasswordToken(String token);

    Cookie register(ClientCreateDTO dto);
    Cookie login(LoginUserDTO dto);
    Cookie updatePasswordForEmployee(String token, String newPassword);
    Cookie logout(Authentication authentication);
}
