package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import br.com.leofonseca.bigchatbr.domain.client.ClientCreateRequestDTO;
import br.com.leofonseca.bigchatbr.domain.enums.DocumentType;
import br.com.leofonseca.bigchatbr.domain.user.User;
import br.com.leofonseca.bigchatbr.domain.user.UserRole;
import br.com.leofonseca.bigchatbr.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public User createUserFromClient(ClientCreateRequestDTO data) {

        User user = new User();
        user.setDocumentId(data.documentId());
        user.setDocumentType(DocumentType.valueOf(data.documentType().toUpperCase()));
        user.setIsActive(data.isActive());
        user.setUserRole(UserRole.CLIENTE);
        user.setPassword(data.password());

        return userRepository.save(user);
    }

    public void updateUserPassword(Long id, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrado"));
        user.setPassword(password);
    }

}
