package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.self.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    ClientResponseDTO register(ClientCreateDTO dto);
    ClientResponseDTO findById(Long id);
    ClientEditDTO findByIdToEdit(Long id);
    List<ClientResponseDTO> findAll(int page, int size, String sort, boolean asc);
    List<ClientResponseDTO> findAll(int page, int size);

    void deleteById(Long id);
    ClientResponseDTO update(long id, ClientEditDTO clientDTO);

    Optional<Client> findEntityById(Long id);
}
