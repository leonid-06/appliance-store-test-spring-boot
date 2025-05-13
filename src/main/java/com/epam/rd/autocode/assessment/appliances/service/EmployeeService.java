package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.self.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    EmployeeResponseDTO create(EmployeeCreateDTO createEmployeeDTO);
    EmployeeResponseDTO findById(Long id);
    EmployeeEditDTO findByIdToEdit(Long id);
    List<EmployeeResponseDTO> findAll(int page, int size, String sort, boolean asc);
    List<EmployeeResponseDTO> findAll(int page, int size);
    void deleteById(Long id);

    EmployeeResponseDTO update(EmployeeEditDTO dto, Long employeeID);

    Optional<Employee> findEntityById(Long clientID);
}
