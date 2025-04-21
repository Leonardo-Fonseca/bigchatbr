package br.com.leofonseca.bigchatbr.config.security;

import br.com.leofonseca.bigchatbr.domain.user.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Slf4j
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
       try {
           log.debug("Gerando token para o usuário: {}", user.getDocumentId());

           Algorithm algorithm = Algorithm.HMAC256(secret);

           String token = JWT.create()
                   .withIssuer("bigchatbr")
                   .withSubject(user.getDocumentId())
                   .withExpiresAt(this.generateExpirationDate())
                   .sign(algorithm);

           log.info("Token gerado com sucesso para o usuário: {}", user.getDocumentId());

           return token;
       } catch (JWTCreationException exception) {
           log.error("Falha ao gerar token para o usuário: {}", user.getDocumentId(), exception);

           throw new RuntimeException("Failed to generate token", exception);
       }
    }

    public String validateToken(String token) {
        try {
            log.debug("Validando token.");
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String subject = JWT.require(algorithm)
                    .withIssuer("bigchatbr")
                    .build()
                    .verify(token)
                    .getSubject();

            log.info("Token válido para o usuário: {}", subject);

            return subject;
        } catch (JWTVerificationException exception) {
            log.error("Token inválido ou expirado", exception);

            return null;
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
