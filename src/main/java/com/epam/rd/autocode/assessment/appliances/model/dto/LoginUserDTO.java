package com.epam.rd.autocode.assessment.appliances.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDTO {

    @NotBlank(message = "{validation.can.not.be-blank}")
    @Email(message = "{validation.can.not.be-blank}")
    private String email;

    @NotBlank(message = "{validation.can.not.be-blank}")
    private String password;
}
