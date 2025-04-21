package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import br.com.leofonseca.bigchatbr.domain.client.ClientCreateRequestDTO;
import br.com.leofonseca.bigchatbr.domain.enums.DocumentType;
import br.com.leofonseca.bigchatbr.domain.user.User;
import br.com.leofonseca.bigchatbr.domain.user.UserRole;
import br.com.leofonseca.bigchatbr.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public User createUserFromClient(ClientCreateRequestDTO data) {
        log.debug("Criando usuário a partir dos dados do cliente: {}", data.documentId());

        User user = new User();
        user.setName(data.name());
        user.setDocumentId(data.documentId());
        user.setDocumentType(DocumentType.valueOf(data.documentType().toUpperCase()));
        user.setIsActive(data.isActive());
        user.setUserRole(UserRole.CLIENTE);
        user.setPassword(data.password());
        User savedUser = userRepository.save(user);

        log.info("Usuário criado com sucesso: {}", savedUser.getDocumentId());

        return savedUser;
    }

    public void updateUserPassword(Long id, String password) {
        log.debug("Atualizando senha do usuário com ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrado"));

        user.setPassword(password);

        log.info("Senha atualizada para o usuário com ID: {}", id);
    }

}
