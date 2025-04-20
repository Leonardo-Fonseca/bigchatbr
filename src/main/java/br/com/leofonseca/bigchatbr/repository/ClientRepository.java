package br.com.leofonseca.bigchatbr.repository;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
