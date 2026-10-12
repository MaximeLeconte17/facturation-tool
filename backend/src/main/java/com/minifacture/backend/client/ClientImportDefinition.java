package com.minifacture.backend.client;

import com.minifacture.backend.imports.ImportDefinition;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ClientImportDefinition implements ImportDefinition<Client> {

    private final ClientRepository clientRepository;

    public ClientImportDefinition(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public int columnCount() {
        return 3;
    }

    @Override
    public Client fromRow(List<String> cells) {
        return new Client(cells.get(0), cells.get(1), cells.get(2));
    }

    @Override
    public String uniqueKey(Client client) {
        return client.getEmail().toLowerCase();
    }

    @Override
    public Set<String> existingKeys() {
        return new HashSet<>(clientRepository.findAllEmails());
    }

    @Override
    public void saveAll(List<Client> clients) {
        clientRepository.saveAll(clients);
    }
}