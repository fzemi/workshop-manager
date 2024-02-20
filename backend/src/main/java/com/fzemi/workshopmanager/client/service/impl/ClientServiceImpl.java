package com.fzemi.workshopmanager.client.service.impl;

import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.repository.ClientRepository;
import com.fzemi.workshopmanager.client.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Optional<Client> findClientById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public List<Client> findClientsBySurname(String surname) {
        return clientRepository.findBySurname(surname);
    }

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client partialUpdate(Long id, Client client) {
        return clientRepository.findById(id).map(existingClient -> {
                    Optional.ofNullable(client.getFirstname()).ifPresent(existingClient::setFirstname);
                    Optional.ofNullable(client.getSurname()).ifPresent(existingClient::setSurname);
                    Optional.ofNullable(client.getPesel()).ifPresent(existingClient::setPesel);
                    Optional.ofNullable(client.getNip()).ifPresent(existingClient::setNip);
                    Optional.ofNullable(client.getPhoneNumber()).ifPresent(existingClient::setPhoneNumber);
                    Optional.ofNullable(client.getEmail()).ifPresent(existingClient::setEmail);
                    Optional.ofNullable(client.getCountry()).ifPresent(existingClient::setCountry);
                    Optional.ofNullable(client.getPostalCode()).ifPresent(existingClient::setPostalCode);
                    Optional.ofNullable(client.getCity()).ifPresent(existingClient::setCity);
                    Optional.ofNullable(client.getAddress()).ifPresent(existingClient::setAddress);
                    Optional.ofNullable(client.getBirthDate()).ifPresent(existingClient::setBirthDate);

                    return clientRepository.save(existingClient);
                })
                .orElse(null);
    }
}
