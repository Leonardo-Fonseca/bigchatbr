-- src/test/resources/test-data.sql

-- 1) Limpa as tabelas para evitar duplicação de PK
DELETE FROM clients;
DELETE FROM users;

-- 2) Insere os usuários exatos
INSERT INTO users (
  id,
  name,
  document_id,
  document_type,
  password,
  user_role,
  is_active,
  created_at,
  updated_at
) VALUES
  (1, 'Leo Fonseca', '10708787908', 'CPF',
     '$2a$12$JbvSOwa40aqosBmY/5/txOQLyacpIhxlXA4tMLQ401bTMUEbTkdMa',
     'ADMIN',   TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'Jose Souza', '11122233344', 'CPF',
     '$2a$12$JbvSOwa40aqosBmY/5/txOQLyacpIhxlXA4tMLQ401bTMUEbTkdMa',
     'CLIENTE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 'Company LTDA', '55566678000112', 'CNPJ',
     '$2a$12$JbvSOwa40aqosBmY/5/txOQLyacpIhxlXA4tMLQ401bTMUEbTkdMa',
     'CLIENTE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3) Insere os clients exatos
INSERT INTO clients (
  id,
  name,
  document_id,
  document_type,
  plan_type,
  balance,
  id_user,
  is_active,
  created_at,
  updated_at
) VALUES
  (1, 'Jose Souza', '11122233344', 'CPF',     'PREPAID',  100.00, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'Company LTDA', '55566678000112', 'CNPJ', 'POSTPAID', 200.00, 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE users   ALTER COLUMN id RESTART WITH 4;
ALTER TABLE clients ALTER COLUMN id RESTART WITH 3;

-- 1) Limpa as tabelas para não duplicar dados em re‑execuções
DELETE FROM messages;
DELETE FROM conversations;

-- 2) Insere duas conversas iniciais entre os clientes 1 e 2
INSERT INTO conversations (
  id,
  client_id,
  recepient_id,
  recipient_name,
  last_message_content,
  last_message_date,
  unread_count
) VALUES
  -- conversa iniciada por José Souza (client_id = 1) -> Company LTDA
  (1, 1, 2, 'Company LTDA', NULL, NULL, 0),
  -- conversa iniciada por Company LTDA (client_id = 2) -> José Souza
  (2, 2, 1, 'Jose Souza',     NULL, NULL, 0);

-- Ajusta o próximo valor de ID da tabela conversations
ALTER TABLE conversations ALTER COLUMN id RESTART WITH 3;

-- 3) Insere quatro mensagens de exemplo
INSERT INTO messages (
  id,
  conversation_id,
  sender_id,
  recipient_id,
  content,
  priority,
  status,
  cost,
  sent_at
) VALUES
  -- Mensagens na conversa 1
  (1, 1, 1, 2, 'Olá, tudo bem?',           'NORMAL', 'SENT',  0.25, CURRENT_TIMESTAMP),
  (2, 1, 2, 1, 'Estou bem, obrigado!',     'URGENT','READ',  0.50, CURRENT_TIMESTAMP),
  -- Mensagens na conversa 2
  (3, 2, 2, 1, 'Preciso de ajuda?',         'NORMAL', 'SENT',  0.25, CURRENT_TIMESTAMP),
  (4, 2, 1, 2, 'Claro, em que posso ajudar?', 'URGENT','READ', 0.50, CURRENT_TIMESTAMP);

-- Ajusta o próximo valor de ID da tabela messages
ALTER TABLE messages ALTER COLUMN id RESTART WITH 5;

-- 4) Atualiza cada conversa para refletir a última mensagem
UPDATE conversations c
SET
  last_message_content = (
    SELECT m.content
    FROM messages m
    WHERE m.conversation_id = c.id
    ORDER BY m.sent_at DESC
    LIMIT 1
  ),
  last_message_date = (
    SELECT m.sent_at
    FROM messages m
    WHERE m.conversation_id = c.id
    ORDER BY m.sent_at DESC
    LIMIT 1
  );
