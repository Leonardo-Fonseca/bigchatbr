package br.com.leofonseca.bigchatbr.repository;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByDocumentId(String documentId);
}
