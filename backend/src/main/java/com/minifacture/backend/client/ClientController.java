package com.minifacture.backend.client;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.minifacture.backend.imports.ExcelImportService;
import com.minifacture.backend.imports.ImportResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientRepository clientRepository;
    private final ExcelImportService excelImportService;
    private final ClientImportDefinition clientImportDefinition;

    public ClientController(ClientRepository clientRepository,
            ExcelImportService excelImportService,
            ClientImportDefinition clientImportDefinition) {
        this.clientRepository = clientRepository;
        this.excelImportService = excelImportService;
        this.clientImportDefinition = clientImportDefinition;
    }

    @GetMapping
    public List<Client> lister() {
        return clientRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client create(@Valid @RequestBody Client client) {
        return clientRepository.save(client);
    }

    @PostMapping("/import")
    public ImportResult importClients(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (file.isEmpty() || fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An .xlsx file is required");
        }
        try {
            return excelImportService.importFile(file.getInputStream(), clientImportDefinition);
        } catch (IOException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unreadable or too large file");
        }
    }

    @GetMapping("/{id}")
    public Client getById(@PathVariable Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable");
        }
        clientRepository.deleteById(id);
    }
}