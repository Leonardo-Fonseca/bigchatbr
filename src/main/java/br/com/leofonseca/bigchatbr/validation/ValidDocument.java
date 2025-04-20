package br.com.leofonseca.bigchatbr.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ValidDocumentValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDocument {
    String message() default "Documento invalido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}