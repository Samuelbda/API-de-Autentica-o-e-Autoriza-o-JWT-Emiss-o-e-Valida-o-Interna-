# API-de-Autentica-o-e-Autoriza-o-JWT-Emiss-o-e-Valida-o-Interna-
API de Autenticação e Autorização JWT 
# 🚀 API de Autenticação e Autorização JWT (Emissão e Validação Interna)

Este projeto implementa uma API Spring Boot robusta para autenticação e autorização usando JSON Web Tokens (JWT). A API é responsável por gerar tokens para usuários autenticados e por validar esses tokens para proteger recursos. O projeto foi construído seguindo as melhores práticas de segurança, testabilidade e documentação.

## 🌟 Funcionalidades

* **Autenticação de Usuário:** Login com nome de usuário e senha para obtenção de um token JWT.
* **Geração de JWTs:** Emissão de tokens JWT com claims de usuário e expiração.
* **Validação Interna de JWTs:** Validação de tokens JWT usando a própria chave secreta da API.
* **Autorização Baseada em Roles:** Proteção de endpoints com base em perfis de usuário (`ADMIN`, `USER`).
* **Banco de Dados em Memória H2:** Facilita o desenvolvimento e testes, com console web.
* **Documentação Interativa:** Geração automática de documentação OpenAPI (Swagger UI).
* **Testes Unitários e de Integração:** Cobertura de testes com JUnit 5 e Mockito.
* **Testes de Carga:** Plano de teste de carga com Apache JMeter.

## 🛠️ Tecnologias Utilizadas

* **Spring Boot 3.x:** Framework principal para o desenvolvimento da API.
* **Spring Security:** Para controle de autenticação e autorização.
* **Spring Data JPA:** Para persistência de dados.
* **H2 Database:** Banco de dados em memória para desenvolvimento e testes.
* **JWT (com auth0/java-jwt):** Para manipulação de tokens.
* **Lombok:** Para reduzir código boilerplate.
* **Springdoc OpenAPI (Swagger UI):** Para documentação automática da API.
* **JUnit 5 & Mockito:** Para testes.
* **Apache JMeter:** Para testes de carga.
* **Maven:** Gerenciador de dependências e build.

## 📦 Como Clonar e Configurar o Projeto

1.  **Clone o Repositório:**
    ```bash
    git clone <URL_DO_SEU_REPOSITORIO_NO_GITHUB>
    cd authserver
    ```

2.  **Configuração do `pom.xml`:**
    * Verifique se todas as dependências listadas no roteiro original estão presentes em seu arquivo `pom.xml`. O Maven irá baixá-las automaticamente na primeira execução.

3.  **Configuração do `application.yml`:**
    * O arquivo `src/main/resources/application.yml` já deve conter as configurações padrão para desenvolvimento (porta, H2, JWT, Springdoc).
    * **Importante:** A `jwt.secret` deve ser uma string longa e complexa. Em produção, use variáveis de ambiente ou serviços de secrets.

## 🚀 Como Executar a Aplicação

1.  **Via Maven (recomendado):**
    ```bash
    mvn spring-boot:run
    ```
    A aplicação estará acessível em `http://localhost:8080`.

2.  **Via IDE (IntelliJ IDEA, VS Code, Eclipse):**
    * Abra o projeto na sua IDE.
    * Navegue até a classe principal `src/main/java/com/example/authserver/DemoApplication.java`.
    * Execute o método `main`.

## 🌐 Acessando Ferramentas e Documentação

Após a aplicação estar em execução, você pode acessar:

* **Console do H2 Database:**
    * Acesse: `http://localhost:8080/h2-console`
    * Credenciais: `Username: sa`, `Password: ` (vazio)
    * Certifique-se de que a URL JDBC esteja como `jdbc:h2:mem:testdb`.
    * Ao iniciar a aplicação, dois usuários (`admin` e `user`) serão automaticamente criados.

* **Documentação OpenAPI (Swagger UI):**
    * Acesse: `http://localhost:8080/swagger-ui.html`
    * Aqui você encontrará todos os endpoints da API, poderá testá-los e ver os esquemas de requisição/resposta.

## ✅ Executando os Testes JUnit

Os testes de integração e unidade são implementados com JUnit 5.

1.  **Via Maven:**
    ```bash
    mvn test
    ```
2.  **Via IDE:**
    * Navegue até o arquivo `src/test/java/com/example/authserver/AuthIntegrationTests.java`.
    * Execute os testes diretamente pela sua IDE.

## 📈 Executando os Testes de Carga com JMeter

Para realizar testes de carga na API, siga os passos abaixo:

1.  **Instale o Apache JMeter:**
    * Baixe em: [Apache JMeter Downloads](https://jmeter.apache.org/download_jmeter.cgi)
    * Descompacte o arquivo e execute `jmeter.bat` (Windows) ou `jmeter.sh` (Linux/macOS) na pasta `bin`.

2.  **Abra o Plano de Teste:**
    * No JMeter, vá em `File` > `Open` e selecione o arquivo `jmeter-tests/login_stress_test.jmx` (ou o nome que você deu ao seu arquivo `.jmx` de teste de carga).

3.  **Execute o Teste:**
    * Clique no botão verde `Start` (ou `Ctrl + R`) na barra de ferramentas do JMeter.

4.  **Visualize os Resultados:**
    * No JMeter, dentro do seu "Thread Group", você pode adicionar "Listeners" como `View Results Tree` e `Summary Report` para analisar o desempenho da API sob carga.

## 🤝 Contribuições

Sinta-se à vontade para abrir issues ou pull requests se encontrar bugs ou tiver sugestões de melhoria.

---

**Desenvolvido por:** <Seu Nome / Nome da Equipe>
**Contato:** <seu.email@example.com>
**Licença:** Apache 2.0 (ou a licença que você preferir)
