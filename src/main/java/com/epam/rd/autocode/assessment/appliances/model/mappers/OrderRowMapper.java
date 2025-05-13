package com.epam.rd.autocode.assessment.appliances.model.mappers;

import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderRowCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderRowResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.self.OrderRow;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderRowMapper {
    OrderRowMapper INSTANCE = Mappers.getMapper(OrderRowMapper.class);
    OrderRowResponseDTO toResponseDTO(OrderRow orderRow);
}
