package br.com.leofonseca.bigchatbr.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalValidationHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> onValidationError(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        var body = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Payload inválido",
                errors
        );

        return ResponseEntity.badRequest().body(body);
    }

    public record ValidationErrorResponse(
            int status,
            String message,
            List<String> errors
    ) {}
}