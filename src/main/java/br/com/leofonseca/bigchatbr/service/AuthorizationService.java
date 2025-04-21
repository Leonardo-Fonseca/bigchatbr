package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String documentId) throws UsernameNotFoundException {
        return Optional.ofNullable(repository.findByDocumentId(documentId))
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado: " + documentId));
    }
}
