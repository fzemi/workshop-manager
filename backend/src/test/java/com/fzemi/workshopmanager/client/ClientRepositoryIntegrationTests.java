package com.fzemi.workshopmanager.client;

import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.repository.ClientRepository;
import com.fzemi.workshopmanager.utils.TestDataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClientRepositoryIntegrationTests {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientRepositoryIntegrationTests(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Test
    public void testThatClientCanBeCreatedAndRecalled() {
        // Given:
        Client client = TestDataUtils.createTestClientA();

        // When:
        clientRepository.save(client);
        Optional<Client> result = clientRepository.findById(client.getId());

        // Then:
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(client);
    }

    @Test
    public void testThatClientCanBeFoundBySurname() {
        Client client = TestDataUtils.createTestClientA();

        clientRepository.save(client);
        List<Client> result = clientRepository.findBySurname(client.getSurname());

        assertThat(result)
                .isNotEmpty()
                .contains(client);
    }
}
