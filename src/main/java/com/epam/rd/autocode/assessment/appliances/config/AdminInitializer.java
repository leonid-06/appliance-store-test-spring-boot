package com.epam.rd.autocode.assessment.appliances.config;

import com.epam.rd.autocode.assessment.appliances.model.enums.Role;
import com.epam.rd.autocode.assessment.appliances.model.enums.UserStatus;
import com.epam.rd.autocode.assessment.appliances.model.self.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminInitializer implements CommandLineRunner {

    private final PasswordEncoder encoder;
    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {

//        String email = System.getenv("ADMIN_EMAIL");
//        String pass = System.getenv("ADMIN_PASSWORD");
        String email = "admin@gmail.com";
        String pass = "hello";

        Employee admin = new Employee();
        admin.setEmail(email);
        admin.setName("Leonid");
        admin.setDepartment("DIRECTOR");
        admin.setPassword(encoder.encode(pass));
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);

        try {
            employeeRepository.save(admin);
        }catch (DataIntegrityViolationException e){
            log.warn("Admin already exists");
        }
    }
}

