package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByEmailException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileUpdatingException;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.mappers.ClientMapper;
import com.epam.rd.autocode.assessment.appliances.model.self.Client;
import com.epam.rd.autocode.assessment.appliances.model.self.User;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.AbstractPersistable_;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repo;
    private final UserService userService;
    private final ClientMapper mapper = ClientMapper.INSTANCE;
    private final PasswordEncoder encoder;

    @Override
    public ClientResponseDTO register(ClientCreateDTO dto) {
        Client client = mapper.toEntity(dto);
        client.setRegistrationDate(LocalDateTime.now());
        client.setPassword(encoder.encode(dto.getPassword()));

        Client saved = repo.save(client);
        log.info("Register client with {} successfully", saved.getId());

        return mapper.toResponseDTO(saved);
    }

    @Override
    public ClientResponseDTO findById(Long id) {
        Client client = repo.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return mapper.toResponseDTO(client);
    }

    @Override
    public ClientEditDTO findByIdToEdit(Long id) {
        return mapper.toEditDTO(repo.findById(id).orElseThrow(EntityNotFoundException::new));
    }


    @Override
    public List<ClientResponseDTO> findAll(int page, int size, String sort, boolean asc) {
        Sort criteria = (asc) ? Sort.by(sort) : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, criteria);
        List<Client> clients = repo.findAll(pageable).getContent();
        return clients.stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<ClientResponseDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Client> clients = repo.findAll(pageable).getContent();
        return clients.stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }


    @Override
    public void deleteById(Long id) {
        repo.findById(id).orElseThrow(EntityNotFoundException::new);
        repo.deleteById(id);
    }

    @Override
    public ClientResponseDTO update(long id, ClientEditDTO dto) {
        log.info("Attempt to edit self cabinet by client with id {}", id);

        Client client = repo.findById(id).orElseThrow(() -> {
            log.warn("Client with id {} not found during update", id);
            return new NotFoundWhileUpdatingException();
        });

        validateClientDoesNotExist(dto, client.getId());
        mapper.updateClientFromDTO(dto, client);
        log.info("Successfully edited client with id {}", id);
        return mapper.toResponseDTO(repo.save(client));
    }

    private void validateClientDoesNotExist(ClientEditDTO dto, Long originalId) {
        Optional<User> user = userService.findByEmail(dto.getEmail());
        if (user.isPresent() && !user.get().getId().equals(originalId)) {
            log.warn("Email {} is already in use", dto.getEmail());
            throw new EntityExistsByEmailException();
        }
    }

    @Override
    public Optional<Client> findEntityById(Long id) {
        return repo.findById(id);
    }
}
