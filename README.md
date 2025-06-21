# 🔐 API de Autenticação e Autorização com JWT - Spring Boot 
Este repositório apresenta uma API construída com **Spring Boot**, focada em **autenticação** e **autorização** utilizando **JWT (JSON Web Tokens)**. 
O projeto inclui proteção de endpoints, geração e validação interna de tokens, além de documentação automática e testes de carga.

---
## 📦 Tecnologias e Dependências

Este projeto utiliza as seguintes bibliotecas e frameworks:

- **Spring Boot Starter Web** – Criação de APIs RESTful
- **Spring Boot Starter Security** – Configuração de segurança
- **Spring Boot Starter OAuth2 Resource Server** – Validação de JWTs
- **Spring Boot Starter Data JPA** – Persistência de dados
- **H2 Database** – Banco de dados em memória
- **Java JWT (Auth0)** – Criação e verificação de tokens
- **Springdoc OpenAPI** – Documentação Swagger
- **Spring Boot DevTools** – Hot reload para desenvolvimento
- **Lombok** – Reduz o código repetitivo (getters, setters, etc.).
- **JUnit 5 + Mockito** – Testes unitários e de integração
- **Apache JMeter** – Testes de carga

---

## 🧱 Estrutura Principal do Projeto
```yaml
src
├── main
│ ├── java/api/jwt
│ │ ├── config
│ │ │ ├── SecurityConfig.java
│ │ │ └── SwaggerConfig.java
│ │ ├── controller
│ │ │ ├── AuthController.java
│ │ │ └── TestProtectedController.java
│ │ ├── model
│ │ │ └── User.java
│ │ ├── repository
│ │ │ └── UserRepository.java
│ │ ├── service
│ │ │ ├── AuthService.java
│ │ │ └── JwtService.java
│ │ └── JwtApplication.java
│ └── resources
│ └── application.yml
└── test/java/api/jwt
├── AuthIntegrationTests.java
└── JwtApplicationTests.java

```

## ⚙️ Configuração do Ambiente

### `application.yml`

A configuração principal da aplicação encontra-se em `src/main/resources/application.yml`. Ela define:

- Conexão com banco H2
- Configurações do JWT (chave secreta e expiração)
- Caminhos do Swagger

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  secret: umaChaveSuperSecretaJWT123!
  expiration: 3600000

springdoc:
  swagger-ui:
    path: /swagger-ui.html

```
⚠️ Em produção, não exponha o segredo JWT diretamente no yml. Use variáveis de ambiente seguras.


## 🧪 Testes Automatizados (JUnit)

A API possui uma suíte robusta de testes de integração com **JUnit 5** e **MockMvc**, cobrindo os principais fluxos de autenticação e segurança.

### 📂 Local dos testes:
`src/test/java/api/jwt/AuthIntegrationTests.java`
### 🔍 O que é testado

| Tipo de Teste                           | Descrição                                                                 |
|-----------------------------------------|---------------------------------------------------------------------------|
| Login válido                            | Verifica se o login com credenciais corretas retorna um JWT válido       |
| Login inválido                          | Verifica se login com senha incorreta retorna HTTP 401                   |
| Acesso sem token                        | Verifica se endpoints protegidos bloqueiam requisições sem autenticação  |
| Acesso com token válido                 | Verifica se endpoints respondem corretamente com token válido            |
| Acesso ADMIN com role USER              | Verifica se usuários sem permissão são bloqueados nos endpoints restritos|
| Acesso ADMIN com role ADMIN             | Verifica se usuário com permissão adequada acessa endpoint restrito      |
| Endpoint público                        | Garante que endpoints públicos como Swagger podem ser acessados livremente |
| Token expirado                          | Simula um token expirado e valida que ele é rejeitado                    |
| Token com role inválida                 | Gera um token com role inválida e verifica que o acesso é negado         |
| Claims do token                         | Verifica se o JWT contém o `username` e a `role` nos claims              |


Esses testes garantem que a segurança e o fluxo de autenticação da API funcionam corretamente, desde o login até a autorização baseada em roles

