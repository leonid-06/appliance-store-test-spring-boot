package com.epam.rd.autocode.assessment.appliances.model.mappers;

import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.self.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.self.Manufacturer;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(uses = ManufacturerMapper.class)
public interface ApplianceMapper {
    ApplianceMapper INSTANCE = Mappers.getMapper(ApplianceMapper.class);

    ApplianceResponseDTO toResponseDTO(Appliance appliance);

    ApplianceEditDTO toEditDTO(Appliance appliance);

    ApplianceEditDTO toEditDTO(ApplianceResponseDTO responseDTO);

    Appliance toEntity(ApplianceCreateDTO dto);


    default void updateEntityFromEditDTO(ApplianceEditDTO dto, @MappingTarget Appliance entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDiscount(dto.getDiscount());
        entity.setCharacteristic(dto.getCharacteristic());
        entity.setPrice(dto.getPrice());
    }

    @AfterMapping
    default void afterMapping(Appliance entity, @MappingTarget ApplianceResponseDTO responseDTO) {
        BigDecimal discount = entity.getPrice().multiply(entity.getDiscount())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        responseDTO.setPriceWithDiscount(entity.getPrice().subtract(discount));
    }
}
