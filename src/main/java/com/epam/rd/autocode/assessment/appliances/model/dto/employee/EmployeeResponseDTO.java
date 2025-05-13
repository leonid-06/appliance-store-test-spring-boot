package com.epam.rd.autocode.assessment.appliances.model.dto.employee;


import com.epam.rd.autocode.assessment.appliances.model.enums.UserStatus;
import com.epam.rd.autocode.assessment.appliances.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String department;
    private Role role;

    private UserStatus status;
}