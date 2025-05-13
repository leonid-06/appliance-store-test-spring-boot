package com.epam.rd.autocode.assessment.appliances.model.dto.client;


import com.epam.rd.autocode.assessment.appliances.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String cardLastFourDigits;
    private Role role;
}