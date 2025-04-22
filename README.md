<h1 align="center">
  Big Chat BR
</h1>

<p align="center">
 <img src="https://img.shields.io/static/v1?label=LinkedIn&message=fonseca-leonardo&color=0077B5&labelColor=000000"/>
 <img src="https://img.shields.io/static/v1?label=Tipo&message=Desafio&color=8257E5&labelColor=000000"/>
 <img src="https://img.shields.io/static/v1?label=Perfil&message=Back-End&color=1dcf4c&labelColor=000000"/>
</p>

API para gerenciar um chat, com CRUD, Fila de Mensagens, Autenticaçao, [desse desafio](https://github.com/RocketBus/quero-ser-clickbus/tree/master/testes/backend-developer) para desenvolvedores que se candidatam para a Irrah Tech.

Perfil de desafio escolhido: Back-End

## Tecnologias

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JDBC + PostgreSQL](https://spring.io/projects/spring-data-jdbc)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [Java JWT (Auth0)](https://github.com/auth0/java-jwt)
- [SpringDoc OpenAPI 3](https://springdoc.org/v2/#spring-webmvc-ui)
- [Docker](https://www.docker.com/)

## Práticas adotadas

- Arquitetura em Camadas
- Testes automatizados
- DTOs e Validacao de Entrada
- Injeção de Dependências
- Geração automática do Swagger com a OpenAPI 3
- Segurança com autenticação JWT
- Containerização com Docker

## Como Executar

### Usando Docker

- Clonar repositório git


- Executar o container:
```
docker-compose up -d
```

A API poderá ser acessada em [localhost:8080](http://localhost:8080).
O Swagger poderá ser visualizado em [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Fila de Mensagens

A fila de mensagens foi implementada em memória usando duas instâncias de `ConcurrentLinkedDeque<Long>`, uma para mensagens urgentes e outra para normais. Um `Semaphore` foi utilizado para coordenar o consumidor. Quando uma mensagem é criada, seu ID é adicionado na fila apropriada e o semáforo libera um "permit", desbloqueando o `MessageProcessor` para processar a mensagem.

Para garantir prioridade sem starvation, adotei a regra **4:1**: até quatro mensagens urgentes são consumidas em sequência (incrementando um contador `cycleCount`), e na quinta vez dá preferência a uma mensagem normal (resetando o contador). Se não houver normais, continua puxando urgentes, sempre respeitando o ciclo.

O acesso às duas deques é thread‑safe por natureza, e apenas a parte que atualiza o `cycleCount` fica em bloco `synchronized` para manter a consistência. Para registrar as estatisticas usei contadores atômicos (`AtomicInteger`) que registram quantas mensagens foram enfileiradas e quantas já foram processadas, permitindo expor estatísticas em tempo real no endpoint ```/queue/status```.

## Autenticação 
A autenticação é feita através de um token JWT, que é gerado quando o cliente se autentica com sucesso. O token é enviado em cada requisição subsequente no cabeçalho `Authorization` como `Bearer <token>`.

Para os endpoints ```/clientes``` é necessário ser um usuario autenticado e com role ```ADMIN``` porem no ```GET /clientes``` caso seja um usuário com role ```CLIENT``` a listagem retorna todos os clientes porem com informações limitadas. O ```GET /clientes/{id}``` tem o mesmo comportamento, pois para o front-end poder listar os clientes que o usuario pode enviar mensagem precisaria de um endpoint aberto. 

```/conversations``` também verifica se é usuario ```ADMIN``` e para retornar todas as conversas ou apenas as do cliente autenticado.

## O que Faltou

Devido ao tempo disponível, nao consegui implementar completamente os testes de unidades e integração. 

Além disso um tratamento mais robusto de erros e exceções poderia ser adicionado, como por exemplo, retornar mensagens mais amigáveis para o usuário.

Também gostaria de ter implementado a fila com um broker de mensagens como kafka para garantir a persistência e escalabilidade.

## API Endpoints

#### Autenticação e Clientes
```
POST   /auth                - Autenticação (CPF/CNPJ)
GET    /clients             - Lista clientes 
POST   /clients             - Cria novo cliente
GET    /clients/:id         - Obtém detalhes de um cliente
PUT    /clients/:id         - Atualiza um cliente
GET    /clients/:id/balance - Consulta saldo/limite do cliente
```

#### Conversas
```
GET    /conversations             - Lista conversas do cliente autenticado
GET    /conversations/:id         - Obtém detalhes de uma conversa
GET    /conversations/:id/messages - Obtém mensagens de uma conversa
```

#### Mensagens
```
POST   /messages            - Envia nova mensagem
GET    /messages            - Lista mensagens com filtros
GET    /messages/:id        - Obtém detalhes de uma mensagem
GET    /messages/:id/status - Verifica status de uma mensagem
```

#### Sistema de Fila
```
GET    /queue/status        - Estatísticas da fila 
```

## Estrutura de Pastas

```
br.com.leofonseca.bigchatbr
 ├─ config          ← Security
 ├─ controller      ← REST
 ├─ domain          ← Entidades JPA
 ├─ enums           ← Enums
 ├─ repository      ← Spring Data
 ├─ service         ← Regras de negócio
 ├─ queue           ← Fila de Mensagens
 ├─ specification   ← Filtros de busca
 └─ validation      ← Validações
```
## Tabelas e Relacionamentos

```SQL
-- Users
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  document_id VARCHAR(50) UNIQUE NOT NULL,
  document_type VARCHAR(20) NOT NULL,
  password VARCHAR(255) NOT NULL,
  user_role VARCHAR(20) NOT NULL,
  is_active BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

-- Clients
CREATE TABLE clients (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  document_id VARCHAR(50) UNIQUE NOT NULL,
  document_type VARCHAR(20) NOT NULL,
  plan_type VARCHAR(20) NOT NULL,
  balance NUMERIC(19,2) NOT NULL,
  invoice NUMERIC(19,2) NOT NULL DEFAULT 0,
  is_active BOOLEAN NOT NULL,
  id_user BIGINT REFERENCES users(id),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

-- Conversations
CREATE TABLE conversations (
  id SERIAL PRIMARY KEY,
  client_id BIGINT REFERENCES clients(id),
  recepient_id BIGINT REFERENCES clients(id),
  recipient_name VARCHAR(255) NOT NULL,
  last_message_content TEXT,
  last_message_date TIMESTAMP,
  unread_count INTEGER NOT NULL
);

-- Messages
CREATE TABLE messages (
  id SERIAL PRIMARY KEY,
  conversation_id BIGINT REFERENCES conversations(id),
  sender_id BIGINT REFERENCES clients(id),
  recipient_id BIGINT REFERENCES clients(id),
  content TEXT NOT NULL,
  priority VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  cost NUMERIC(19,2) NOT NULL,
  sent_at TIMESTAMP NOT NULL
);
```

- Relacionamentos

```
clients.id_user → users.id (1:1)
conversations.client_id → clients.id (N:1)
conversations.recepient_id → clients.id (N:1)
messages.conversation_id → conversations.id (N:1)
messages.sender_id → clients.id (N:1)
messages.recipient_id → clients.id (N:1)
```