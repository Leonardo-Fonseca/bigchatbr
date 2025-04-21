package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.client.*;
import br.com.leofonseca.bigchatbr.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    public ResponseEntity<List<?>> getAllClients() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                List<ClientResponseDTO> response = clientService.list();
                return ResponseEntity.ok(response);
            } else {
                List<ClientLimitedResponseDTO> response = clientService.listLimited();
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(
            @PathVariable("id") Long id
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                ClientResponseDTO response = clientService.findById(id);
                return ResponseEntity.ok(response);
            } else {
                ClientLimitedResponseDTO response = clientService.findLimitedById(id);
                return ResponseEntity.ok(response);
            }
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
