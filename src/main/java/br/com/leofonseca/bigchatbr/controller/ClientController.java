package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import br.com.leofonseca.bigchatbr.domain.client.ClientCreateRequestDTO;
import br.com.leofonseca.bigchatbr.domain.client.ClientResponseDTO;
import br.com.leofonseca.bigchatbr.domain.client.ClientUpdateRequestDTO;
import br.com.leofonseca.bigchatbr.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(
            @RequestBody @Valid  ClientCreateRequestDTO request
    ) {
        ClientResponseDTO response = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable("id") Long id,
            @RequestBody @Valid ClientUpdateRequestDTO request
    ) {
        ClientResponseDTO response = clientService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        List<ClientResponseDTO> response = clientService.list();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(
            @PathVariable("id") Long id
    ) {
        ClientResponseDTO response = clientService.findById(id);
        return ResponseEntity.ok(response);
    }


}
