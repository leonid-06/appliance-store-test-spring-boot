package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileUpdatingException;
import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.enums.Category;
import com.epam.rd.autocode.assessment.appliances.model.mappers.ApplianceMapper;
import com.epam.rd.autocode.assessment.appliances.model.self.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.self.Employee;
import com.epam.rd.autocode.assessment.appliances.model.self.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epam.rd.autocode.assessment.appliances.controller.CommonNames.*;

@Service
@Slf4j
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository repo;
    private final ApplianceMapper mapper;
    private final ManufacturerRepository manufacturerRepo;

    @Autowired
    public ApplianceServiceImpl(ApplianceRepository repo, ManufacturerRepository manufacturerRepo) {
        this.repo = repo;
        this.manufacturerRepo = manufacturerRepo;
        this.mapper = ApplianceMapper.INSTANCE;
    }

    @Override
    public ApplianceResponseDTO update(Long id, ApplianceEditDTO dto) {
        Appliance appliance = repo.findById(id).orElseThrow(NotFoundWhileUpdatingException::new);
        mapper.updateEntityFromEditDTO(dto, appliance);

        return mapper.toResponseDTO(repo.save(appliance));
    }

    @Override
    public ApplianceResponseDTO create(ApplianceCreateDTO dto) {
        log.info("Attempting to add new appliance: {}", dto);

        Manufacturer manufacturer = manufacturerRepo.findById(dto.getManufacturerId()).orElseThrow(EntityNotFoundException::new);

        Appliance appliance = mapper.toEntity(dto);
        appliance.setManufacturer(manufacturer);

        Appliance saved = repo.save(appliance);
        log.info("Appliance added successfully: {}", appliance.getId());
        return mapper.toResponseDTO(saved);
    }

    @Override
    public ApplianceResponseDTO findById(Long id) {
        Optional<Appliance> optionalAppliance = repo.findById(id);
        if (optionalAppliance.isEmpty()) throw new EntityNotFoundException("Appliance with id " + id + " not found");
        return mapper.toResponseDTO(optionalAppliance.get());
    }


    @Override
    public List<ApplianceResponseDTO> findAll(int page, int size, String sort, boolean asc) {
        Sort criteria = (asc) ? Sort.by(sort) : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, criteria);
        List<Appliance> appliances = repo.findAll(pageable).getContent();
        return appliances.stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplianceResponseDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Appliance> appliances = repo.findAll(pageable).getContent();
        return appliances.stream().map(mapper::toResponseDTO).collect(Collectors.toList());

    }

//    @Override
//    public List<ApplianceResponseDTO> findAll() {
//        List<Appliance> appliances = repo.findAll();
//        return appliances
//    }

//    @Override
//    public void save(ApplianceResponseDTO applianceDTO) {
//        log.info("Attempt to save appliance: {}", applianceDTO.getId());
//        if (repo.findById(applianceDTO.getId()).isEmpty()) {
//            throw new ApplianceForEmployeeNotFoundException("Error saving appliance, appliance with ID " + applianceDTO.getId() + " not found");
//        }
//        repo.save(mapper.toEntityMe(applianceDTO));
//        log.info("Appliance added successfully: {}", applianceDTO.getId());
//    }

//    @Override
//    public void deleteById(Long id) {
//        log.warn("Attempt to delete appliance with ID: {}", id);
//        if (repo.findById(id).isEmpty()) {
//            throw new ApplianceForEmployeeNotFoundException("Error deleting appliance, appliance with id " + id + " not found");
//        }
//        repo.deleteById(id);
//        log.info("Appliance deleted successfully: {}", id);
//    }

    @Override
    public List<ApplianceResponseDTO> findTop5ByDiscountGreaterThan(double v) {
        return repo.findByDiscountGreaterThan(v).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplianceResponseDTO> findTop5ByOrderBySalesCountDesc() {
        return repo.findTop5ByOrderBySalesCountDesc(PageRequest.of(0, 10)).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public Page<ApplianceResponseDTO> getAppliances(
            String category,
            boolean discounted,
            String sortField,
            Pageable pageable) {

        Specification<Appliance> spec = Specification.where(null);

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root,
                             query,
                             cb)
                    -> cb.equal(root.get("category"), Category.valueOf(category)));
        }
        if (discounted) {
            spec = spec.and((root,
                             query,
                             cb)
                    -> cb.gt(root.get("discount"), BigDecimal.ZERO));
        }

        Pageable sortedPageable = createSortedPageable(pageable, sortField);

        return repo.findAll(spec, sortedPageable)
                .map(mapper::toResponseDTO);
    }


    private Pageable createSortedPageable(Pageable pageable, String sortField) {
        if (sortField == null || sortField.isBlank()) {
            return pageable;
        }
        Sort sort = resolveSort(sortField);
        return sort != null
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort)
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }

    private Sort resolveSort(String sortField) {
        return switch (sortField) {
            case EXPENSIVE -> Sort.by("price").descending();
            case CHEAP -> Sort.by("price").ascending();
            case POWER -> Sort.by("power").descending();
            default -> null;
        };
    }



    @Override
    public ApplianceEditDTO findByIdToEdit(Long id) {
        return repo.findById(id).map(mapper::toEditDTO).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Page<ApplianceResponseDTO> findAllWithPagination(int page, int size) {
        Page<Appliance> retVal = repo.findAll(PageRequest.of(page, size));
        return retVal.map(mapper::toResponseDTO);
    }


//    @Override
//    public ApplianceResponseDTO update(ApplianceResponseDTO dto) {
//
//        log.info("Attempt to update appliance with ID: {}", dto.getId());
//
//        Optional<Appliance> optionalAppliance = repo.findById(dto.getId());
//        if (optionalAppliance.isPresent()) {
//            Appliance appliance = optionalAppliance.get();
//            appliance = mapper.toEntity(appliance, dto);
//            repo.save(appliance);
//            log.info("Appliance updated successfully: {}", appliance.getId());
//        } else {
//            throw new ApplianceForEmployeeNotFoundException("Error updating appliance, appliance with id " + dto.getId() + " not found");
//        }
//    }
}
