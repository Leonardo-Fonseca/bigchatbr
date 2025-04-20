package br.com.leofonseca.bigchatbr.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = AtLeastOneNotNullValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneNotNull {
    String message() default "Deve ser informado ao menos um campo";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
