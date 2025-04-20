package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.client.*;
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
        try {
            ClientResponseDTO response = clientService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable("id") Long id,
            @RequestBody @Valid ClientUpdateRequestDTO request
    ) {
        try {
            ClientResponseDTO response = clientService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        try {
            List<ClientResponseDTO> response = clientService.list();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar clientes", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(
            @PathVariable("id") Long id
    ) {
        try {
            ClientResponseDTO response = clientService.findById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<ClientBalanceDTO> getClientBalance(
            @PathVariable("id") Long id
    ) {
        try {
            return ResponseEntity.ok(clientService.getBalanceById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


}
