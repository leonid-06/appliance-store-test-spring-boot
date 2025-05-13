package com.epam.rd.autocode.assessment.appliances.model.dto.client;

import com.epam.rd.autocode.assessment.appliances.model.validation.CreditCard;
import com.epam.rd.autocode.assessment.appliances.model.validation.PhoneNumber;
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
public class ClientEditDTO {

    @NotBlank(message = "{validation.name.notBlank}")
    @Size(max = 30, message = "{validation.name.size}")
    private String name;

    @NotBlank(message = "{validation.email.invalid}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.phone.notBlank}")
    @PhoneNumber(message = "{validation.phone.invalid}")
    private String phoneNumber;

    @CreditCard(message = "{validation.card.invalid}")
    private String card;
}
