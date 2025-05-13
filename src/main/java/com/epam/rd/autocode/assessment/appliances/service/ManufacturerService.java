package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.dto.manufacturer.ManufacturerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ManufacturerService {
    ManufacturerDTO create(ManufacturerDTO createDTO);
    Page<ManufacturerDTO> findAllActive(int page, int size);
    List<ManufacturerDTO> findAllDeleted();
    ManufacturerDTO findById(Long id);
    void deleteById(Long id);
    ManufacturerDTO update(Long id, ManufacturerDTO dto);
}
