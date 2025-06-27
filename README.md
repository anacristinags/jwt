# 🔐 API de Autenticação e Autorização com JWT - Spring Boot

Este repositório apresenta uma API desenvolvida com **Spring Boot**, focada em **autenticação** e **autorização** utilizando **JWT (JSON Web Tokens)**.  
O projeto inclui proteção de endpoints, geração e validação interna de tokens, documentação automática via Swagger e testes de carga com JMeter.

---

## 🗂 Índice

1. [Sobre o Projeto](#sobre-o-projeto)  
2. [Tecnologias & Dependências](#tecnologias--dependências)  
3. [Arquitetura](#arquitetura)  
4. [Configuração](#configuração-do-ambiente)  
5. [Como Rodar](#como-rodar-o-projeto)  
6. [Autenticação & Endpoints](#autenticação--endpoints)  
7. [Testes Automatizados](#testes-automatizados-junit)  
8. [Testes de Carga](#testes-de-carga-com-apache-jmeter)  
9. [Documentação Swagger](#documentação-swagger)  
10. [Produzido por](#produzido-por)

---

## 📖 Sobre o Projeto

Este projeto é um exemplo prático de aplicação **segura com JWT** no ecossistema Spring Boot 3.x. A API emite tokens assinados com chave secreta, valida internamente e restringe o acesso a endpoints com base em **roles** (`USER`, `ADMIN`). O projeto inclui:

- API para autenticação (`/auth/login`) e validação de token (`/auth/validate`)  
- Controller com rotas protegidas como exemplo (`/api/protected`)  
- Banco **H2** para persistência em memória  
- Testes automatizados com **JUnit 5** e **Mockito**  
- Testes de carga com **Apache JMeter**

---

## 📦 Tecnologias e Dependências

| Grupo        | Biblioteca                                   | Descrição                     |
| ------------ | -------------------------------------------- | ----------------------------- |
| Core         | `spring-boot-starter-web`                    | Criação de API REST           |
| Segurança    | `spring-boot-starter-security`               | Autenticação e autorização    |
|              | `spring-boot-starter-oauth2-resource-server` | Validação de JWT              |
| Persistência | `spring-boot-starter-data-jpa`               | JPA/Hibernate                 |
|              | `com.h2database:h2`                          | Banco em memória              |
| JWT          | `com.auth0:java-jwt`                         | Geração e validação de token  |
| Documentação | `springdoc-openapi-starter-webmvc-ui`        | Swagger UI                    |
| Dev Tools    | `spring-boot-devtools`                       | Hot reload                    |
| Utilitários  | `lombok`                                     | Redução de código boilerplate |
| Testes       | `spring-boot-starter-test`                   | JUnit 5 e Mockito             |
| Carga        | **Apache JMeter**                            | Simulação de requisições      |

---

## 🏗️ Arquitetura

```text
┌───────────────┐ login ┌──────────────┐ validate ┌───────────────┐
│ AuthController ├──────► AuthService ├───────────► JwtService    │
└───────────────┘        └────────────┘             │
        ▲                                    verify ▼
     protected                             save/fetch
┌───────────────┐  JPA  ┌──────────────┐       ┌──────────────┐
│ UserRepository ├─────►│    H2 DB     │       │ TestProtected│
└───────────────┘       └──────────────┘       └──────────────┘
```

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
> ⚠️ Em produção, não exponha o segredo JWT diretamente no yml. Use variáveis de ambiente seguras.

## 💾 Como rodar o projeto

### 1. Clonar o repositório

```bash
git clone https://github.com/anacristinags/jwt
```

### 2. Configurar o banco de dados

Este projeto utiliza o banco de dados **H2**, que é um banco relacional em memória, ideal para testes e desenvolvimento local.

O banco H2 já está configurado no arquivo `src/main/resources/application.yml`

> ⚠️ Observações:
> O banco é volátil: seus dados são apagados quando a aplicação é finalizada.

#### 🧪 Acessando o Console Web do H2
1. Inicie a aplicação 
2. Acesse o console H2 no navegador:
`http://localhost:8080/h2-console`
3. Preencha os dados da conexão com:
   
| Campo         | Valor                |
| ------------- | -------------------- |
| **JDBC URL**  | `jdbc:h2:mem:testdb` |
| **User Name** | `sa`                 |
| **Password**  | *(deixe em branco)*  |


4. Clique em Connect

## 🔐 Autenticação & Endpoints
Fluxo
POST /auth/login → envia username + password ⇒ recebe JWT

Inclua o token no header Authorization: Bearer <token>

Acesse recursos protegidos, por exemplo: GET /api/protected/user

Usuários de Teste
| Login   | Senha      | Role    |
| ------- | ---------- | ------- |
| `user`  | `password` | `USER`  |
| `admin` | `123456`   | `ADMIN` |


Endpoints
| Método | Rota             | Descrição    |
| ------ | ---------------- | ------------ |
| POST   | `/auth/login`    | Gera token   |
| POST   | `/auth/validate` | Valida token |

| Método | Rota                   | Acesso          |
| ------ | ---------------------- | --------------- |
| GET    | `/api/protected/user`  | `USER`, `ADMIN` |
| GET    | `/api/protected/admin` | `ADMIN`         |



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

##  📈 Testes de Carga com Apache JMeter
Para avaliar o desempenho da API sob múltiplas requisições simultâneas.

Etapas:
Instale o Apache JMeter

Abra o arquivo `Arvore de Resultados.jmx` (salvo na pasta `testes_JMeter`)

Com a aplicação rodando, clique em Start

Analise os resultados via Summary Report ou View Results Tree
![Image](https://github.com/user-attachments/assets/26fc324f-4986-46be-89ca-2113bf982158)

![Image](https://github.com/user-attachments/assets/e98d6c55-1bca-4a86-9119-bc00ee47076b)

## 📖 Documentação Swagger
O projeto possui documentação automática com o Swagger UI, gerada via springdoc-openapi.
#### Acesse em: `http://localhost:8080/swagger-ui.html`

![Image](https://github.com/user-attachments/assets/b0880632-3d6e-4fff-808f-d438fabe0aa4)

![Image](https://github.com/user-attachments/assets/68b62553-ca4e-45cf-a70b-be62f463e4dd)

### 🔐 Como autenticar no Swagger com JWT

Para acessar endpoints protegidos via Swagger, é necessário informar o token JWT no cabeçalho das requisições:

1. Vá até o endpoint `POST /auth/login`
2. Clique em **"Try it out"**
3. Informe os parâmetros:
   - `username`: ex: `admin`
   - `password`: ex: `123456`
4. Clique em **Execute** e copie o token retornado na resposta (sem aspas)
![Image](https://github.com/user-attachments/assets/1b27b934-5e61-4e6d-9dca-839d172e5e20)
5. Volte ao topo da Swagger UI e clique em **Authorize**
6. No campo que aparece, preencha com o token gerado.
7. Clique em **Authorize** e depois em **Close**

Pronto! Agora você poderá testar todos os endpoints protegidos diretamente pela interface Swagger.
![Image](https://github.com/user-attachments/assets/61c2efa6-f9b6-4176-bb90-c21e838afe35)

![Image](https://github.com/user-attachments/assets/edd0c6a2-0882-4762-9fa3-6258a9468a4a)

![Image](https://github.com/user-attachments/assets/1df16cee-7c74-49bb-b9ef-230c8c76ee42)

## 👩‍💻 Produzido por
Este projeto foi desenvolvido por Ana Cristina, para a matéria de Arquitetura de Aplicações Web
