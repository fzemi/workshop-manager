package com.fzemi.workshopmanager.client.service;

import com.fzemi.workshopmanager.client.dto.ClientDTO;
import com.fzemi.workshopmanager.client.entity.Client;

import java.util.List;

public interface ClientService {
    /**
     * @return all clientDTOs
     */
    List<ClientDTO> findAll();

    /**
     * @param id client's id
     * @return clientDTO with the given id
     * @throws com.fzemi.workshopmanager.client.exception.ClientNotFoundException if client with the given id does not exist
     */
    ClientDTO findClientById(Long id);

    /**
     * @param surname client's surname
     * @return list of clientsDTOs with the given surname
     */
    List<ClientDTO> findClientsBySurname(String surname);

    /**
     * Creates a new client
     *
     * @param client new client
     * @return created clientDTO
     */
    ClientDTO save(Client client);


    /**
     * Fully updates an existing client
     *
     * @param client client with updated fields
     * @return updated clientDTO
     * @throws com.fzemi.workshopmanager.client.exception.ClientNotFoundException if client with the given id does not exist
     */
    ClientDTO fullUpdate(Client client);

    /**
     * Partially updates an existing client
     *
     * @param id     existing client's id
     * @param client client with updated fields
     * @return updated clientDTO
     * @throws com.fzemi.workshopmanager.client.exception.ClientNotFoundException if client with the given id does not exist
     */
    ClientDTO partialUpdate(Long id, Client client);
}
