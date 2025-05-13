package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByEmailException;
import com.epam.rd.autocode.assessment.appliances.model.dto.LoginUserDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.enums.UserStatus;
import com.epam.rd.autocode.assessment.appliances.model.self.Employee;
import com.epam.rd.autocode.assessment.appliances.model.self.User;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.security.JwtUtil;
import com.epam.rd.autocode.assessment.appliances.service.AuthService;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.expiration.minutes}")
    private int JWT_EXPIRATION_IN_MINUTES;

    @Value("${jwt.name}")
    private String COOKIE_NAME;

    private final UserService userService;
    private final EmployeeRepository employeeRepo;
    private final ClientService clientService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;


    @Override
    public Cookie register(ClientCreateDTO dto) {

        if (userService.existsByEmail(dto.getEmail())) {
            throw new EntityExistsByEmailException();
        }
        ClientResponseDTO registered = clientService.register(dto);
        String clientId = registered.getId().toString();
        String role = registered.getRole().name();

        log.info("Registered new client {}", clientId);
        return getCookie(clientId, role);
    }

    @Override
    public Cookie login(LoginUserDTO dto) {
        Optional<User> optionalUser = userService.findByEmail(dto.getEmail());
        if (optionalUser.isEmpty()) throw new BadCredentialsException("No such user " + dto.getEmail());

        User user = optionalUser.get();

        if (encoder.matches(dto.getPassword(), user.getPassword())) {
            return getCookie(user.getId().toString(), user.getRole().name());
        } else {
            throw new BadCredentialsException("Wrong password for user with ID " + user.getId());
        }
    }

    @Override
    public boolean isValidSetupPasswordToken(String token) {
        Optional<Employee> optionalEmployee = employeeRepo.findBySetupPasswordToken(token);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            return LocalDateTime.now().isBefore(employee.getTemporaryPasswordExpiryTime());
        } else {
            return false;
        }
    }

    @Override
    public Cookie updatePasswordForEmployee(String token, String newPassword) {
        Optional<Employee> optionalEmployee = employeeRepo.findBySetupPasswordToken(token);
        // log.warn("Someone tried to update password for invalid token {}", token);
        if (optionalEmployee.isEmpty()) throw new EntityNotFoundException(); // todo log more detailed

        Employee employee = optionalEmployee.get();
        employee.setPassword(encoder.encode(newPassword));
        employee.setSetupPasswordToken(null);
        employee.setTemporaryPasswordExpiryTime(LocalDateTime.now());
        employee.setStatus(UserStatus.ACTIVE);
        employeeRepo.save(employee);
        log.info("Password updated for user {}", employee.getId());
        return getCookie(employee.getId().toString(), employee.getRole().name());
    }

    @Override
    public Cookie logout(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String userID = (String) authentication.getPrincipal();
            log.info("Logged out user {}", userID);
        } else {
            log.warn("Someone tried to log out for invalid authentication");
        }

        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setMaxAge(0);

        return cookie;
    }


    private Cookie getCookie(String userId, String role) {
        Cookie cookie;
        String token = jwtUtil.generateToken(userId, role);
        cookie = new Cookie(COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setMaxAge(JWT_EXPIRATION_IN_MINUTES * 60);
        return cookie;
    }
}
