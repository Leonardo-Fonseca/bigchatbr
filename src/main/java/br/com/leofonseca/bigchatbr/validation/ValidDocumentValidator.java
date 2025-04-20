package br.com.leofonseca.bigchatbr.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class ValidDocumentValidator implements ConstraintValidator<ValidDocument, Object> {
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true;
        }
        try {
            Field documentIdField = object.getClass().getDeclaredField("documentId");
            Field documentTypeField = object.getClass().getDeclaredField("documentType");
            documentIdField.setAccessible(true);
            documentTypeField.setAccessible(true);

            Object docIdObj = documentIdField.get(object);
            Object docTypeObj = documentTypeField.get(object);

            // Se ambos os campos forem nulos, o objeto é considerado válido
            if (docIdObj == null && docTypeObj == null) {
                return true;
            }
            // Verifica se os campos documentId e documentType não são nulos
            // para ser valido é preciso ter os dois campos preenchidos
            if (docIdObj == null || docTypeObj == null) {
                return false;
            }

            String docId = docIdObj.toString();
            String docType = docTypeObj.toString();

            if ("CPF".equalsIgnoreCase(docType)) {
                return isValidCPF(docId);
            } else if ("CNPJ".equalsIgnoreCase(docType)) {
                return isValidCNPJ(docId);
            }
            return false;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }

    // Validação para CPF: somente números e exatamente 11 dígitos.
    // TODO: Implementar validação mais robusta para CPF (ex: dígitos verificadores).
    private boolean isValidCPF(String cpf) {
        if (cpf == null) return false;
        if (!cpf.matches("\\d+")) return false;
        return cpf.length() == 11;
    }

    // Validação para CNPJ: somente números e exatamente 14 dígitos.
    // TODO: Implementar validação mais robusta para CNPJ (ex: dígitos verificadores).
    private boolean isValidCNPJ(String cnpj) {
        if (cnpj == null) return false;
        if (!cnpj.matches("\\d+")) return false;
        return cnpj.length() == 14;
    }
}