package br.com.leofonseca.bigchatbr.repository;

import br.com.leofonseca.bigchatbr.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByDocumentId(String documentId);
}
