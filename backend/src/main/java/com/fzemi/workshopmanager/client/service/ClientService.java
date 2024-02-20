package com.fzemi.workshopmanager.client.service;

import com.fzemi.workshopmanager.client.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    /**
     * @return all clients
     */
    List<Client> findAll();

    /**
     * @param id client's id
     * @return client with the given id
     */
    Optional<Client> findClientById(Long id);

    /**
     * @param surname client's surname
     * @return list of clients with the given surname
     */
    List<Client> findClientsBySurname(String surname);

    /**
     * Creates a new client or fully updates an existing one
     *
     * @param client new client
     * @return created client
     */
    Client save(Client client);

    /**
     * Partially updates an existing client
     *
     * @param id existing client's id
     * @param client client with updated fields
     * @return updated client
     */
    Client partialUpdate(Long id, Client client);
}
