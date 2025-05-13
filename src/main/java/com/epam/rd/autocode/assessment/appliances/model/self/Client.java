package com.epam.rd.autocode.assessment.appliances.model.self;

import com.epam.rd.autocode.assessment.appliances.model.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)

@Entity
public class Client extends User {
    private String card;
    private String phoneNumber;
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
    private List<Order> orders;

    public Client(String name, String email, String phoneNumber, String card, String password, LocalDateTime registrationDate) {
        setRole(Role.CLIENT);
        setName(name);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setPassword(password);
        setCard(card);
        setRegistrationDate(registrationDate);
    }

    public Client() {
        setRole(Role.CLIENT);
    }
}
