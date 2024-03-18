package com.fzemi.workshopmanager.client.service;

import com.fzemi.workshopmanager.client.dto.ClientDTO;
import com.fzemi.workshopmanager.client.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    /**
     * @return all clientDTOs
     */
    List<ClientDTO> findAll();

    /**
     * @param id client's id
     * @return clientDTO with the given id
     */
    Optional<ClientDTO> findClientById(Long id);

    /**
     * @param surname client's surname
     * @return list of clientsDTOs with the given surname
     */
    List<ClientDTO> findClientsBySurname(String surname);

    /**
     * Creates a new client or fully updates an existing one
     *
     * @param client new client
     * @return created clientDTO
     */
    ClientDTO save(Client client);

    /**
     * Partially updates an existing client
     *
     * @param id     existing client's id
     * @param client client with updated fields
     * @return updated clientDTO
     */
    ClientDTO partialUpdate(Long id, Client client);
}
