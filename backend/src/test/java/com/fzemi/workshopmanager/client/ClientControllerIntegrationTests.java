package com.fzemi.workshopmanager.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fzemi.workshopmanager.client.dto.ClientDTO;
import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.service.ClientService;
import com.fzemi.workshopmanager.utils.TestDataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
// resolves lazy loading issues for clientService
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@WithMockUser(username = "user")
public class ClientControllerIntegrationTests {
    private final MockMvc mockMvc;
    private final ClientService clientService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ClientControllerIntegrationTests(
            MockMvc mockMvc,
            ClientService clientService
    ) {
        this.mockMvc = mockMvc;
        this.clientService = clientService;
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testThatGetClientsReturnsStatus200AndAllUsersDTOs() throws Exception {
        Client clientA = TestDataUtils.createTestClientA();
        Client clientB = TestDataUtils.createTestClientB();
        ClientDTO savedClientADTO = clientService.save(clientA);
        ClientDTO savedClientBDTO = clientService.save(clientB);

        List<ClientDTO> allClients = List.of(savedClientADTO, savedClientBDTO);
        String expectedJson = objectMapper.writeValueAsString(allClients);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/clients")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.content().json(expectedJson)
        );
    }

    @Test
    public void testThatGetClientByIdReturnsStatus200AndClientDTO() throws Exception {
        Client client = TestDataUtils.createTestClientA();
        ClientDTO savedClientDTO = clientService.save(client);

        String expectedJson = objectMapper.writeValueAsString(savedClientDTO);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/clients/" + savedClientDTO.id())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.content().json(expectedJson)
        );
    }

    @Test
    public void testThatGetNonExistingClientReturnsStatus404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/clients/123")
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatCreateClientReturnsStatus201AndSavedClientDTO() throws Exception {
        Client client = TestDataUtils.createTestClientA();
        ClientDTO savedClientDTO = TestDataUtils.createTestClientADTO();

        String clientJson = objectMapper.writeValueAsString(client);
        String resultJson = objectMapper.writeValueAsString(savedClientDTO);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(clientJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.content().json(resultJson)
        );
    }

    @Test
    public void testThatFullUpdateClientReturnsStatus200AndUpdatedClientDTO() throws Exception {
        Client client = TestDataUtils.createTestClientA();
        clientService.save(client);
        Client updatedClient = TestDataUtils.createTestClientA("Adam");
        ClientDTO updatedClientDTO = TestDataUtils.createTestClientADTO("Adam");

        String updatedClientJson = objectMapper.writeValueAsString(updatedClient);
        String resultJson = objectMapper.writeValueAsString(updatedClientDTO);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/clients/" + updatedClient.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedClientJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.content().json(resultJson)
        );
    }

    @Test
    public void testThatFullUpdateNonExistingClientReturnsStatus404() throws Exception {
        Client client = TestDataUtils.createTestClientA();

        String clientJson = objectMapper.writeValueAsString(client);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/clients/" + client.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(clientJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatPartialUpdateClientReturnsStatus200AndUpdatedClientDTO() throws Exception {
        Client client = TestDataUtils.createTestClientA();
        ClientDTO clientDTO = TestDataUtils.createTestClientADTO("Adam");
        clientService.save(client);
        Client updatedFields = Client.builder()
                .id(1L)
                .firstname("Adam")
                .build();

        String clientJson = objectMapper.writeValueAsString(updatedFields);
        String resultJson = objectMapper.writeValueAsString(clientDTO);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v1/clients/" + updatedFields.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(clientJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.content().json(resultJson)
        );
    }
}
