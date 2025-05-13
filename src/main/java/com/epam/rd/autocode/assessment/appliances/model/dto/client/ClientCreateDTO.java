package com.epam.rd.autocode.assessment.appliances.model.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreateDTO {

    @NotBlank(message = "{validation.can.not.be-blank}")
    @Size(min = 2, max = 30, message = "{validation.name.size}")
    private String name;

    @NotBlank(message = "{validation.can.not.be-blank}")
    @Email(message = "{validation.can.not.be-blank}")
    private String email;

    @NotBlank(message = "{validation.can.not.be-blank}")
    private String password;
}