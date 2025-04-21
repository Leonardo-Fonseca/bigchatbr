package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.domain.client.*;
import br.com.leofonseca.bigchatbr.domain.enums.DocumentType;
import br.com.leofonseca.bigchatbr.domain.user.User;
import br.com.leofonseca.bigchatbr.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final UserService userService;

    public ClientResponseDTO create(ClientCreateRequestDTO data) {
        log.info("Criando novo cliente com nome: {}", data.name());
        User newUser = userService.createUserFromClient(data);
        PlanType planType = PlanType.valueOf(data.planType().toUpperCase());
        DocumentType documentType = DocumentType.valueOf(data.documentType().toUpperCase());

        Client newClient = new Client();
        newClient.setName(data.name());
        newClient.setDocumentId(data.documentId());
        newClient.setDocumentType(documentType.name());
        newClient.setPlanType(planType.name());
        newClient.setBalance(data.balance());
        newClient.setIsActive(data.isActive());
        newClient.setUser(newUser);

        Client savedClient = clientRepository.save(newClient);
        log.info("Cliente criado com ID: {}", savedClient.getId());
        return new ClientResponseDTO(savedClient);
    }

    public ClientResponseDTO update(Long id, ClientUpdateRequestDTO data) {
        log.info("Atualizando cliente com ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        if (data.password() != null) {
            userService.updateUserPassword(client.getUser().getId(), data.password());
            log.info("Senha do usuário atualizada para cliente ID: {}", id);
        }

        updateClientFields(client, data);

        Client savedClient = clientRepository.save(client);
        log.info("Cliente atualizado com sucesso. ID: {}", savedClient.getId());
        return new ClientResponseDTO(savedClient);
    }

    private void updateClientFields(Client client, ClientUpdateRequestDTO data) {
        // Centraliza as atualizações para melhorar legibilidade
        client.setName(data.name() != null ? data.name() : client.getName());
        client.setDocumentId(data.documentId() != null ? data.documentId() : client.getDocumentId());
        client.setDocumentType(data.documentType() != null
                ? DocumentType.valueOf(data.documentType().toUpperCase()).name()
                : client.getDocumentType());
        client.setPlanType(data.planType() != null
                ? PlanType.valueOf(data.planType().toUpperCase()).name()
                : client.getPlanType());
        client.setBalance(data.balance() != null ? data.balance() : client.getBalance());
        client.setIsActive(data.isActive() != null ? data.isActive() : client.getIsActive());
    }

    public List<ClientResponseDTO> list() {
        return this.clientRepository.findAll().stream().map(ClientResponseDTO::new).toList();
    }

    public List<ClientLimitedResponseDTO> listLimited() {
        return clientRepository.findAll().stream().map(ClientLimitedResponseDTO::new).toList();
    }

    public ClientResponseDTO findById(Long id) {
        return new ClientResponseDTO(this.findClientById(id));
    }

    public ClientLimitedResponseDTO findLimitedById(Long id) {
        return new ClientLimitedResponseDTO(this.findClientById(id));
    }

    public Client findClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    public Client findClientByDocumentId(String documentId) {
        return clientRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    public ClientBalanceDTO getBalanceById(Long id) {
        return new ClientBalanceDTO(this.findClientById(id).getBalance());
    }

}
