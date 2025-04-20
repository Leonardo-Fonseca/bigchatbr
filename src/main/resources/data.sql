INSERT INTO users (id, name, document_id, document_type, password, user_role, is_active, created_at, updated_at)
SELECT t.* FROM (
    SELECT 1 AS id, 'Leo Fonseca' AS name, '10708787908' AS document_id, 'CPF' AS document_type, '$2a$12$JbvSOwa40aqosBmY/5/txOQLyacpIhxlXA4tMLQ401bTMUEbTkdMa' AS password, 'ADMIN' AS user_role, TRUE AS is_active, CURRENT_TIMESTAMP AS created_at, CURRENT_TIMESTAMP AS updated_at
    UNION ALL
    SELECT 2, 'Jose Souza', '11122233344', 'CPF', '$2a$12$JbvSOwa40aqosBmY/5/txOQLyacpIhxlXA4tMLQ401bTMUEbTkdMa', 'CLIENTE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT 3, 'Company LTDA', '55566678000112', 'CNPJ', '$2a$12$JbvSOwa40aqosBmY/5/txOQLyacpIhxlXA4tMLQ401bTMUEbTkdMa', 'CLIENTE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
) t
WHERE NOT EXISTS (SELECT 1 FROM users);

INSERT INTO clients (id, name, document_id, document_type, plan_type, balance, invoice, id_user, is_active, created_at, updated_at)
SELECT t.* FROM (
    SELECT 1 AS id, 'Jose Souza' AS name, '11122233344' AS document_id, 'CPF' AS document_type, 'PREPAID' AS plan_type, 100.00 AS balance, 0 AS invoice, 2 AS id_user, TRUE AS is_active, CURRENT_TIMESTAMP AS created_at, CURRENT_TIMESTAMP AS updated_at
    UNION ALL
    SELECT 2, 'Company LTDA', '55566678000112', 'CNPJ', 'POSTPAID', 200.00, 0, 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
) t
WHERE NOT EXISTS (SELECT 1 FROM clients);

ALTER TABLE users   ALTER COLUMN id RESTART WITH 4;
ALTER TABLE clients ALTER COLUMN id RESTART WITH 3;