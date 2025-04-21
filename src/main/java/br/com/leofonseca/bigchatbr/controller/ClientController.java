package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.client.*;
import br.com.leofonseca.bigchatbr.service.ClientService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints para operações de clientes.")
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    @Operation(summary = "Criar novo cliente", description = "Cria um novo cliente a partir dos dados fornecidos.")
    public ResponseEntity<ClientResponseDTO> createClient(
            @RequestBody @Valid ClientCreateRequestDTO request
    ) {
        try {
            ClientResponseDTO response = clientService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados do cliente para o ID informado.")
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
    @Operation(
        summary = "Listar clientes",
        description = "Retorna a lista completa ou limitada de clientes conforme o perfil do usuário.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de clientes completa caso o usuario autenticado seja ADMIN.",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = ClientResponseDTO.class)
                    )
                )
            ),
            @ApiResponse(
                    responseCode = "206",
                    description = "Lista de clientes limitada caso o usuario autenticado seja CLIENTE.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ClientLimitedResponseDTO.class)
                            )
                    )
            )
        }
    )
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
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar cliente",
        description = "Busca as informações de um cliente pelo ID, com dados completos ou limitados conforme o perfil.",
        responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "LDetalhes do cliente completa caso o usuario autenticado seja ADMIN.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "206",
                    description = "Detalhes do cliente limitado caso o usuario autenticado seja CLIENTE.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientLimitedResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
        }
    )
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
    @Operation(summary = "Saldo do cliente", description = "Retorna o saldo do cliente para o ID informado.")
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
