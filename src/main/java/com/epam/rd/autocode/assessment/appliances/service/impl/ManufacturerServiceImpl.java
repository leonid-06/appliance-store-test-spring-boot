package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByNameException;
import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByPhoneException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileDeletingException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileUpdatingException;
import com.epam.rd.autocode.assessment.appliances.model.dto.manufacturer.ManufacturerDTO;
import com.epam.rd.autocode.assessment.appliances.model.mappers.ManufacturerMapper;
import com.epam.rd.autocode.assessment.appliances.model.self.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerSpec;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository repo;
    private final ManufacturerMapper mapper = ManufacturerMapper.INSTANCE;

    @Override
    public Page<ManufacturerDTO> findAllActive(int page, int size) {
        return repo.findAll(ManufacturerSpec.isNotDeleted(), PageRequest.of(page, size)).map(mapper::toDTO);
    }

    @Override
    public List<ManufacturerDTO> findAllDeleted() {
        return repo.findAll(ManufacturerSpec.isDeleted()).stream().map(mapper::toDTO).toList();
    }

    @Override
    public ManufacturerDTO findById(Long id) {
        return repo.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public ManufacturerDTO create(ManufacturerDTO dto) {
        log.info("Registering new manufacturer with name: {}", dto.getName());
        validateManufacturerDoesNotExistForCreate(dto);

        Manufacturer manufacturer = mapper.toEntity(dto);
        Manufacturer saved = repo.save(manufacturer);
        log.info("Manufacturer registered successfully with ID: {}", manufacturer.getId());
        return mapper.toDTO(saved);
    }

    @Override
    public ManufacturerDTO update(Long id, ManufacturerDTO dto) {
        log.info("Updating manufacturer with ID: {}", id);
        Manufacturer manufacturer = repo.findById(id)
                .orElseThrow(NotFoundWhileUpdatingException::new);
        validateManufacturerDoesNotExistForUpdate(manufacturer, dto);

        mapper.updateEntityFromDto(dto, manufacturer);
        Manufacturer saved = repo.save(manufacturer);
        log.info("Manufacturer with ID {} updated successfully", id);
        return mapper.toDTO(saved);
    }

    private void validateManufacturerDoesNotExistForCreate(ManufacturerDTO dto) {
        if (repo.existsByName(dto.getName())) {
            throw new EntityExistsByNameException();
        }

        if (repo.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new EntityExistsByPhoneException();
        }
    }

    private void validateManufacturerDoesNotExistForUpdate(Manufacturer manufacturerFromDB, ManufacturerDTO dto) {
        Optional<Manufacturer> existByName = repo.findByName(dto.getName());
        if (existByName.isPresent() && !existByName.get().getId().equals(manufacturerFromDB.getId())) {
            throw new EntityExistsByNameException();
        }

        Optional<Manufacturer> existByPhone = repo.findByPhoneNumber(dto.getPhoneNumber());
        if (existByPhone.isPresent() && !existByPhone.get().getId().equals(manufacturerFromDB.getId())) {
            throw new EntityExistsByPhoneException();
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting manufacturer with ID: {}", id);

        Manufacturer manufacturer = repo.findById(id).orElseThrow(EntityNotFoundException::new);
        manufacturer.setDeleted(true);

        log.info("Manufacturer with ID {} deleted successfully", id);

//        if (!repo.existsById(id)) {
//            log.warn("Delete failed: manufacturer with ID {} not found", id);
//            throw new NotFoundWhileDeletingException();
//        }
//        repo.deleteById(id);
//        log.info("Manufacturer with ID {} deleted successfully", id);
    }
}
