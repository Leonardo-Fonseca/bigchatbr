package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String documentId) throws UsernameNotFoundException {
        log.debug("Buscando usuário com documentId: {}", documentId);

        UserDetails userDetails = Optional.ofNullable(repository.findByDocumentId(documentId))
                .orElseThrow(() -> {

                    log.error("Usuário não encontrado: {}", documentId);

                    return new UsernameNotFoundException("Usuário não encontrado: " + documentId);

                });

        log.info("Usuário encontrado: {}", documentId);

        return userDetails;
    }
}
