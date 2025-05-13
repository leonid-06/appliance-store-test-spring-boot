package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByEmailException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileDeletingException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileUpdatingException;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.enums.UserStatus;
import com.epam.rd.autocode.assessment.appliances.model.mappers.EmployeeMapper;
import com.epam.rd.autocode.assessment.appliances.model.self.Employee;
import com.epam.rd.autocode.assessment.appliances.model.self.User;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repo;
    private final EmployeeMapper mapper = EmployeeMapper.INSTANCE;
    private final PasswordEncoder encoder;
    private final UserService userService;


    @Override
    public EmployeeResponseDTO create(EmployeeCreateDTO dto) {

        String mockPassword = UUID.randomUUID().toString();
        String setupPasswordToken = UUID.randomUUID().toString();

        Employee employee = mapper.toEntity(dto);
        employee.setPassword(mockPassword);
        employee.setTemporaryPasswordExpiryTime(LocalDateTime.now().plusHours(6));
        employee.setSetupPasswordToken(setupPasswordToken);

        Employee saved = repo.save(employee);
        sendEmail(saved);
        return mapper.toResponseDTO(saved);
    }

    @Override
    public EmployeeResponseDTO findById(Long id) {
        Employee employee = repo.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return mapper.toResponseDTO(employee);
    }

    @Override
    public EmployeeEditDTO findByIdToEdit(Long id) {
        return repo.findById(id).map(mapper::toEditDTO).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<EmployeeResponseDTO> findAll(int page, int size, String sort, boolean asc) {
        Sort criteria = (asc) ? Sort.by(sort) : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, criteria);
        List<Employee> clients = repo.findAll(pageable).getContent().stream()
                .filter(employee -> employee.getStatus() != UserStatus.DELETED).toList();;
        return clients.stream().map(mapper::toResponseDTO).collect(Collectors.toList());

    }

    @Override
    public List<EmployeeResponseDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Employee> clients = repo.findAll(pageable)
                .getContent().stream()
                .filter(employee -> employee.getStatus() != UserStatus.DELETED).toList();
        return clients.stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDTO update(EmployeeEditDTO dto, Long employeeID) {
        log.info("Attempt to update employee with id {} ", employeeID);

        Employee employee = repo.findById(employeeID).orElseThrow(() -> {
            log.warn("Employee with id {} not found during update", employeeID);
            return new NotFoundWhileUpdatingException();
        });

        validateEmployeeDoesNotExist(dto, employeeID);

        mapper.updateEmployeeFromDto(dto, employee);
        log.info("Successfully self edited employee with id {}", employeeID);
        return mapper.toResponseDTO(repo.save(employee));
    }

    @Override
    public Optional<Employee> findEntityById(Long clientID) {
        return repo.findById(clientID);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Attempting to delete employee with id {}", id);

        Employee employee = repo.findById(id).orElseThrow(NotFoundWhileDeletingException::new);
        employee.setStatus(UserStatus.DELETED);
//        repo.findById(id).ifPresentOrElse(repo::delete, () -> {
//            log.warn("Employee with id {} not found during delete", id);
//            throw new NotFoundWhileDeletingException();
//        });
    }

    private void validateEmployeeDoesNotExist(EmployeeEditDTO dto, Long originalId) {
        Optional<User> user = userService.findByEmail(dto.getEmail());
        if (user.isPresent() && !user.get().getId().equals(originalId)) {
            log.warn("Email {} is  already in use", dto.getEmail());
            throw new EntityExistsByEmailException();
        }
    }

    private void sendEmail(Employee savedEmployee) {
        String text = String.format("Hello, Dear %s, " +
                        "We have registered you in our system, " +
                        "please follow the link %s and update your password", savedEmployee.getName(),
                "http://localhost:8080/password-reset?token=" + savedEmployee.getSetupPasswordToken());
        log.info(text);
    }
}
