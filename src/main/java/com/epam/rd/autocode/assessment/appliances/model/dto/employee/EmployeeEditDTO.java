package com.epam.rd.autocode.assessment.appliances.model.dto.employee;


import com.epam.rd.autocode.assessment.appliances.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEditDTO {

    @NotBlank(message = "{validation.name.notBlank}")
    @Size(max = 30, message = "{validation.name.size}")
    private String name;

    @NotBlank(message = "{validation.email.invalid}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.department.invalid}")
    private String department;

    @NotNull(message = "{validation.role.notBlank}")
    private Role role;

    // true if user changed password after registration
    private boolean approved;
}