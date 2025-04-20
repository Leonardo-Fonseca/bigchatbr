-- src/test/resources/test-data.sql

-- 1) Limpa as tabelas para evitar duplicação de PK
DELETE FROM clients;
DELETE FROM users;

-- 2) Insere os usuários exatos
INSERT INTO users (
  id,
  document_id,
  document_type,
  password,
  user_role,
  is_active,
  created_at,
  updated_at
) VALUES
  (1, '10708787908', 'CPF',
     '$2a$12$JbvSOwa40aqosBmY/5/txOQLyacpIhxlXA4tMLQ401bTMUEbTkdMa',
     'ADMIN',   TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, '11122233344', 'CPF',
     '$2a$12$JbvSOwa40aqosBmY/5/txOQLyacpIhxlXA4tMLQ401bTMUEbTkdMa',
     'CLIENTE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, '55566678000112', 'CNPJ',
     '$2a$12$JbvSOwa40aqosBmY/5/txOQLyacpIhxlXA4tMLQ401bTMUEbTkdMa',
     'CLIENTE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3) Insere os clients exatos
INSERT INTO clients (
  id,
  document_id,
  document_type,
  plan_type,
  balance,
  id_user,
  is_active,
  created_at,
  updated_at
) VALUES
  (1, '11122233344', 'CPF',     'PREPAID',  100.00, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, '55566678000112', 'CNPJ', 'POSTPAID', 200.00, 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE users   ALTER COLUMN id RESTART WITH 4;
ALTER TABLE clients ALTER COLUMN id RESTART WITH 3;