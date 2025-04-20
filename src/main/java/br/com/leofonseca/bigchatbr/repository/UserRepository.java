package br.com.leofonseca.bigchatbr.repository;

import br.com.leofonseca.bigchatbr.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
