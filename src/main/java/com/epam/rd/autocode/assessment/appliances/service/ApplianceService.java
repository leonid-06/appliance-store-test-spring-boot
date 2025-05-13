package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ApplianceService {

    // todo need to implement
    ApplianceResponseDTO update(Long id, ApplianceEditDTO dto);
//    ApplianceResponseDTO deleteById(Long id);


    ApplianceResponseDTO create(ApplianceCreateDTO dto);
    ApplianceResponseDTO findById(Long id);
    List<ApplianceResponseDTO> findAll(int page, int size, String sort, boolean asc);
    List<ApplianceResponseDTO> findAll(int page, int size);

    List<ApplianceResponseDTO> findTop5ByDiscountGreaterThan(double v);
    List<ApplianceResponseDTO> findTop5ByOrderBySalesCountDesc();
    Page<ApplianceResponseDTO> getAppliances(String category, boolean discounted, String sortField, Pageable pageable);

    ApplianceEditDTO findByIdToEdit(Long id);


    // paging
    Page<ApplianceResponseDTO> findAllWithPagination(int page, int size);
}
