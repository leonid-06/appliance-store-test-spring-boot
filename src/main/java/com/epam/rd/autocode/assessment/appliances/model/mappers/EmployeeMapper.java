package com.epam.rd.autocode.assessment.appliances.model.mappers;

import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.self.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    EmployeeResponseDTO toResponseDTO(Employee employee);
    EmployeeEditDTO toEditDTO(Employee employee);

    Employee toEntity(EmployeeResponseDTO dto);
    Employee toEntity(EmployeeCreateDTO dto);

    void updateEmployeeFromDto(EmployeeEditDTO dto, @MappingTarget Employee employee);
    void updateEmployeeFromDto(EmployeeCreateDTO dto, @MappingTarget Employee employee);
}
