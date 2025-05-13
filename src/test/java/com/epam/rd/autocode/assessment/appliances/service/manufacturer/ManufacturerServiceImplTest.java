package com.epam.rd.autocode.assessment.appliances.service.manufacturer;

import com.epam.rd.autocode.assessment.appliances.JsonTestLoader;
import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByNameException;
import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByPhoneException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileUpdatingException;
import com.epam.rd.autocode.assessment.appliances.model.dto.manufacturer.ManufacturerDTO;
import com.epam.rd.autocode.assessment.appliances.model.mappers.ManufacturerMapper;
import com.epam.rd.autocode.assessment.appliances.model.self.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerSpec;
import com.epam.rd.autocode.assessment.appliances.service.impl.ManufacturerServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceImplTest {

    @Mock
    private ManufacturerRepository repository;

    private ManufacturerMapper mapper = ManufacturerMapper.INSTANCE;

    @InjectMocks
    private ManufacturerServiceImpl service;

    private static ManufacturerDTO testDto;
    private static Manufacturer testEntity;

    private static ManufacturerDTO testDtoToUpdate;
    private static Manufacturer testEntityUpdated;

    @BeforeAll
    static void setUp() {
        testDto = new ManufacturerDTO(null, "TestName", "TestDesc", "+123456789");
        testEntity = new Manufacturer(10L, "TestName", "TestDesc", "+123456789");

        testDtoToUpdate = new ManufacturerDTO(null, "AnotherName", "AnotherDesc", "+123456789");
        testEntityUpdated = new Manufacturer(10L, "AnotherName", "AnotherDesc", "+123456789");

    }

    @Nested
    @DisplayName("Find Tests")
    class FindTests {

        @Test
        void findByExistingId() {
            Manufacturer mock = getTestManufacturer();
            Long id = mock.getId();

            when(repository.findById(id)).thenReturn(Optional.of(mock));

            ManufacturerDTO result = service.findById(id);
            assertNotNull(result);
            assertTrue(isEqual(result, mapper.toDTO(mock)));

            verify(repository).findById(id);
        }

        @Test
        void findByNotExistentId() {
            Long nonExistentId = 404L;
            when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> service.findById(nonExistentId)
            );

            verify(repository).findById(nonExistentId);
        }

        @Test
        void findAllActive_ReturnsPage() {

            int page = 0;
            int size = 10;
            PageRequest pageRequest = PageRequest.of(page, size);
            List<Manufacturer> activeManufacturers = getActiveManufacturers();

            Page<Manufacturer> pageMock = new PageImpl<>(activeManufacturers, pageRequest, activeManufacturers.size());
            when(repository.findAll(ManufacturerSpec.isNotDeleted(), pageRequest)).thenReturn(pageMock);

            // Act
            Page<ManufacturerDTO> result = service.findAllActive(page, size);

            assertNotNull(result);
            assertEquals(activeManufacturers.size(), result.getTotalElements());
            assertEquals(activeManufacturers.get(0).getName(), result.getContent().get(0).getName());
            assertEquals(activeManufacturers.get(0).getPhoneNumber(), result.getContent().get(0).getPhoneNumber());

            verify(repository).findAll(ManufacturerSpec.isNotDeleted(), pageRequest);
        }


    }

    @Nested
    @DisplayName("Create Tests")
    class CreateTests {

        @Test
        void createManufacturer() {
            when(repository.existsByName(testDto.getName())).thenReturn(false);
            when(repository.existsByPhoneNumber(testDto.getPhoneNumber())).thenReturn(false);
            when(repository.save(mapper.toEntity(testDto))).thenReturn(testEntity);

            ManufacturerDTO result = service.create(testDto);

            // Assert
            assertTrue(isEqual(result, mapper.toDTO(testEntity)));

            verify(repository).existsByName(testDto.getName());
            verify(repository).existsByPhoneNumber(testDto.getPhoneNumber());
            verify(repository).save(mapper.toEntity(testDto));
        }

        @Test
        void createManufacturerWithExistingName_ThrowsException() {
            when(repository.existsByName(testDto.getName())).thenReturn(true);

            assertThrows(EntityExistsByNameException.class, () -> service.create(testDto));

            verify(repository).existsByName(testDto.getName());
            verify(repository, never()).existsByPhoneNumber(any());
            verify(repository, never()).save(any());
        }

        @Test
        void createManufacturerWithExistingPhoneNumber_ThrowsException() {
            when(repository.existsByName(testDto.getName())).thenReturn(false);
            when(repository.existsByPhoneNumber(testDto.getPhoneNumber())).thenReturn(true);

            assertThrows(EntityExistsByPhoneException.class, () -> service.create(testDto));

            verify(repository).existsByName(testDto.getName());
            verify(repository).existsByPhoneNumber(testDto.getPhoneNumber());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Tests")
    class DeleteTests {

        @Test
        void deleteById_Exists_SetsDeletedFlag() {
            Long id = testEntity.getId();

            when(repository.findById(id)).thenReturn(Optional.of(testEntity));
            service.deleteById(id);

            assertTrue(testEntity.isDeleted());
            verify(repository).findById(id);
        }

        @Test
        void deleteById_NotExistent_ThrowsException() {
            Long id = 404L;

            when(repository.findById(id)).thenReturn(Optional.empty());
            assertThrows(
                    EntityNotFoundException.class,
                    () -> service.deleteById(id)
            );
        }
    }


    @Nested
    @DisplayName("Update Tests")
    class UpdateTests {

        @Test
        void shouldUpdateManufacturerSuccessfully() {
            Long id = testEntity.getId();

            when(repository.findById(id)).thenReturn(Optional.of(testEntity));
            when(repository.findByName(testDtoToUpdate.getName())).thenReturn(Optional.empty());
            when(repository.findByPhoneNumber(testDtoToUpdate.getPhoneNumber())).thenReturn(Optional.empty());

            Manufacturer toSave = mapper.toEntity(testDtoToUpdate);
            toSave.setId(id);

            when(repository.save(toSave)).thenReturn(testEntityUpdated);

            ManufacturerDTO result = service.update(id, testDtoToUpdate);

            assertNotNull(result);
            assertTrue(isEqual(result, mapper.toDTO(testEntityUpdated)));

            verify(repository).findById(id);
            verify(repository).findByName(testDtoToUpdate.getName());
            verify(repository).findByPhoneNumber(testDtoToUpdate.getPhoneNumber());
            verify(repository).save(toSave);
        }

        @Test
        void shouldThrowNotFoundWhileUpdating() {
            Long id = 404L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThrows(NotFoundWhileUpdatingException.class, () -> service.update(id, testDtoToUpdate));

            verify(repository).findById(id);
            verify(repository, never()).save(any());
        }

        //
        @Test
        void shouldThrowWhenNameExists() {
            Long id = testEntity.getId();

            Manufacturer takenNameManufacturer =
                    new Manufacturer(123L, testDtoToUpdate.getName(), "desc", "123456789");

            when(repository.findById(id)).thenReturn(Optional.of(testEntity));
            when(repository.findByName(testDtoToUpdate.getName())).thenReturn(Optional.of(takenNameManufacturer));

            assertThrows(EntityExistsByNameException.class, () -> service.update(id, testDtoToUpdate));

            verify(repository).findById(id);
            verify(repository).findByName(testDtoToUpdate.getName());
            verify(repository, never()).findByPhoneNumber(any());
            verify(repository, never()).save(any());
        }


        @Test
        void shouldThrowWhenPhoneExists() {
            Long id = testEntity.getId();

            Manufacturer takenPhoneManufacturer =
                    new Manufacturer(123L, "Some name", testDtoToUpdate.getPhoneNumber(), "123456789");

            when(repository.findById(id)).thenReturn(Optional.of(testEntity));
            when(repository.findByName(testDtoToUpdate.getName())).thenReturn(Optional.empty());
            when(repository.findByPhoneNumber(testDtoToUpdate.getPhoneNumber())).thenReturn(Optional.of(takenPhoneManufacturer));

            assertThrows(EntityExistsByPhoneException.class, () -> service.update(id, testDtoToUpdate));

            verify(repository).findById(id);
            verify(repository).findByName(testDtoToUpdate.getName());
            verify(repository).findByPhoneNumber(testDtoToUpdate.getPhoneNumber());
            verify(repository, never()).save(any());
        }

        @Test
        void shouldUpdateDespiteExistingName() {
            Long id = testEntity.getId();

            when(repository.findById(id)).thenReturn(Optional.of(testEntity));
            when(repository.findByName(testDtoToUpdate.getName())).thenReturn(Optional.of(testEntity));
            when(repository.findByPhoneNumber(testDtoToUpdate.getPhoneNumber())).thenReturn(Optional.empty());

            Manufacturer toSave = mapper.toEntity(testDtoToUpdate);
            toSave.setId(id);

            when(repository.save(toSave)).thenReturn(testEntityUpdated);

            ManufacturerDTO result = service.update(id, testDtoToUpdate);

            assertNotNull(result);
            assertTrue(isEqual(result, mapper.toDTO(testEntityUpdated)));


            verify(repository).findById(id);
            verify(repository).findByName(testDtoToUpdate.getName());
            verify(repository).findByPhoneNumber(testDtoToUpdate.getPhoneNumber());
            verify(repository).save(toSave);
        }

        @Test
        void shouldUpdateDespiteExistingPhone() {
            Long id = testEntity.getId();

            when(repository.findById(id)).thenReturn(Optional.of(testEntity));
            when(repository.findByName(testDtoToUpdate.getName())).thenReturn(Optional.empty());
            when(repository.findByPhoneNumber(testDtoToUpdate.getPhoneNumber())).thenReturn(Optional.of(testEntity));

            Manufacturer toSave = mapper.toEntity(testDtoToUpdate);
            toSave.setId(id);

            when(repository.save(toSave)).thenReturn(testEntityUpdated);

            // Act
            ManufacturerDTO result = service.update(id, testDtoToUpdate);

            assertNotNull(result);
            assertTrue(isEqual(result, mapper.toDTO(testEntityUpdated)));

            verify(repository).findById(id);
            verify(repository).findByName(testDtoToUpdate.getName());
            verify(repository).findByPhoneNumber(testDtoToUpdate.getPhoneNumber());
            verify(repository).save(toSave);
        }

    }


    private boolean isEqual(ManufacturerDTO result, ManufacturerDTO original) {
        return original.getId().equals(result.getId()) &&
                original.getName().equals(result.getName()) &&
                original.getDescription().equals(result.getDescription()) &&
                original.getPhoneNumber().equals(result.getPhoneNumber());
    }

    private Manufacturer getTestManufacturer() {
        return JsonTestLoader.load("/manufacturer/test-manufacturer.json", Manufacturer.class);
    }

    private ManufacturerDTO getTestManufacturerDTO() {
        return JsonTestLoader.load("/manufacturer/test-manufacturer-dto.json", ManufacturerDTO.class);
    }

    private List<Manufacturer> getDeletedManufacturers() {
        return JsonTestLoader.loadList("/manufacturer/test-manufacturers.json", Manufacturer[].class)
                .stream().filter(Manufacturer::isDeleted).collect(Collectors.toList());

    }

    private List<Manufacturer> getActiveManufacturers() {
        return JsonTestLoader.loadList("/manufacturer/test-manufacturers.json", Manufacturer[].class)
                .stream().filter(manufacturer -> !manufacturer.isDeleted()).collect(Collectors.toList());
    }
}