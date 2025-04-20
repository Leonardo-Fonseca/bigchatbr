package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.domain.client.*;
import br.com.leofonseca.bigchatbr.domain.enums.DocumentType;
import br.com.leofonseca.bigchatbr.domain.user.User;
import br.com.leofonseca.bigchatbr.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final UserService userService;

    public ClientResponseDTO create(ClientCreateRequestDTO data) {
        User newUser = userService.createUserFromClient(data);

        Client newClient = new Client();
        newClient.setDocumentId(data.documentId());
        newClient.setDocumentType(DocumentType.valueOf(data.documentType().toUpperCase()));
        newClient.setPlanType(PlanType.valueOf(data.planType().toUpperCase()));
        newClient.setBalance(data.balance());
        newClient.setIsActive(data.isActive());
        newClient.setUser(newUser);

        Client savedClient = clientRepository.save(newClient);

        return new ClientResponseDTO(savedClient);
    }

    public ClientResponseDTO update(
            Long id,
            ClientUpdateRequestDTO data
    ) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        // Se password vier na request atualiza a senha do usuario
        if (data.password() != null) {
            userService.updateUserPassword(client.getUser().getId(), data.password());
        }

        // Atualiza os campos do Cliente se existir na request
        // TODO: Melhorar legibilidade desse codigo.
        client.setDocumentId(data.documentId() != null ? data.documentId() : client.getDocumentId());
        client.setDocumentType(data.documentType() != null
                ? DocumentType.valueOf(data.documentType().toUpperCase())
                : client.getDocumentType());
        client.setPlanType(data.planType() != null
                ? PlanType.valueOf(data.planType().toUpperCase())
                : client.getPlanType());
        client.setBalance(data.balance() != null ? data.balance() : client.getBalance());
        client.setIsActive(data.isActive() != null ? data.isActive() : client.getIsActive());

        Client savedClient = clientRepository.save(client);
        return new ClientResponseDTO(savedClient);
    }

    public List<ClientResponseDTO> list() {
        List<ClientResponseDTO> clientList = this.clientRepository.findAll().stream().map(ClientResponseDTO::new).toList();
        return clientList;
    }
    public ClientResponseDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return new ClientResponseDTO(client);
    }
}
