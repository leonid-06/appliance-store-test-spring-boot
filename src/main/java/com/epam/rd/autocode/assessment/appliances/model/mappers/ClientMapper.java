package com.epam.rd.autocode.assessment.appliances.model.mappers;

import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.self.Client;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    ClientResponseDTO toResponseDTO(Client client);
    ClientEditDTO toEditDTO(Client client);
    Client toEntity(ClientResponseDTO dto);

    @Mapping(target = "password", ignore = true)
    Client toEntity(ClientCreateDTO dto);

    void updateClientFromDTO(ClientEditDTO clientEditDTO, @MappingTarget Client client);
    void updateClientFromDTO(ClientCreateDTO clientEditDTO, @MappingTarget Client client);

    @AfterMapping
    default void mapCardLastFourDigits(Client client, @MappingTarget ClientResponseDTO dto) {
        if (client.getCard() != null && client.getCard().length() >= 4) {
            dto.setCardLastFourDigits(client.getCard().substring(client.getCard().length() - 4));
        }
    }
}
