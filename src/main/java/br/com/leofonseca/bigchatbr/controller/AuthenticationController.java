package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.config.security.TokenService;
import br.com.leofonseca.bigchatbr.domain.user.AuthenticationDTO;
import br.com.leofonseca.bigchatbr.domain.user.LoginResponseDTO;
import br.com.leofonseca.bigchatbr.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    @Operation(
        summary = "Realizar login",
        description = "Autentica o usuário e retorna o token de acesso.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Token gerado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de autenticação inválidos")
        }
    )
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.documentId(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
