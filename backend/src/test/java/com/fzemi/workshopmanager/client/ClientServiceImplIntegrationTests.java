package com.fzemi.workshopmanager.client;

import com.fzemi.workshopmanager.client.dto.ClientDTO;
import com.fzemi.workshopmanager.client.dto.ClientMapper;
import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.repository.ClientRepository;
import com.fzemi.workshopmanager.client.service.impl.ClientServiceImpl;
import com.fzemi.workshopmanager.utils.TestDataUtils;
import com.fzemi.workshopmanager.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientServiceImplIntegrationTests {
    @InjectMocks
    private ClientServiceImpl clientService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private ClientMapper clientMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testThatClientCanBeCreated() {
        // Given:
        Client client = TestDataUtils.createTestClientA();
        client.setId(null);
        ClientDTO clientDTO = TestDataUtils.createTestClientADTO();
        Client savedClient = TestDataUtils.createTestClientA();

        // Mock the calls
        Mockito.when(clientRepository.save(client)).thenReturn(savedClient);
        Mockito.when(clientMapper.toClientDTO(savedClient)).thenReturn(clientDTO);

        // When:
        ClientDTO result = clientService.save(client);

        // Then:
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(clientDTO);
    }

    @Test
    public void testThatClientCanBeFullyUpdated() {
        Client client = TestDataUtils.createTestClientA(); // with id = 1
        client.setFirstname("Adam");
        ClientDTO underTestClientDTO = TestDataUtils.createTestClientADTO("Adam");

        Mockito.when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        Mockito.when(clientRepository.save(client)).thenReturn(client);
        Mockito.when(clientMapper.toClientDTO(client)).thenReturn(underTestClientDTO);

        ClientDTO result = clientService.save(client);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(underTestClientDTO);
    }

    @Test
    public void testThatClientCanBePartiallyUpdated() {
        Client client = TestDataUtils.createTestClientA();
        Client updatedClientFields = Client.builder()
                .id(1L)
                .firstname("Adam")
                .build();
        Client clientAfterUpdate = TestDataUtils.createTestClientA("Adam");
        ClientDTO clientAfterUpdateDTO = TestDataUtils.createTestClientADTO("Adam");

        Mockito.when(clientRepository.findById(updatedClientFields.getId())).thenReturn(Optional.of(client));
        Mockito.when(clientRepository.save(client)).thenReturn(clientAfterUpdate);
        Mockito.when(clientMapper.toClientDTO(clientAfterUpdate)).thenReturn(clientAfterUpdateDTO);

        ClientDTO result = clientService.partialUpdate(updatedClientFields.getId(), updatedClientFields);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(clientAfterUpdateDTO);
    }
}
