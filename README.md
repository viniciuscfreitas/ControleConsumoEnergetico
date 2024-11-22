# Controle de Consumo Energético

Este é um projeto de software para controlar o consumo energético, desenvolvido com interface gráfica em Java. Ele permite registrar, acompanhar e analisar o consumo energético, além de configurar metas de consumo.

---

## Funcionalidades

- **Registro de Consumo:** Permite adicionar dados sobre o consumo diário, com descrição e valor.
- **Configuração de Metas:** Permite definir metas de consumo (diária, semanal ou mensal).
- **Gráficos e Estatísticas:** Gráficos interativos e estatísticas detalhadas sobre o consumo.
- **Exportação de Relatórios:** Exportação dos dados para arquivos CSV.
- **Histórico:** Visualização do histórico completo de consumo.
- **Sistema de Login:** Sistema de login com controle de usuários no banco de dados.

---

## Requisitos

1. **Java:** JDK 8 ou superior.
2. **MySQL:** Banco de dados configurado e acessível.
3. **Driver JDBC:** Arquivo `mysql-connector-java-*.jar` incluído no projeto.
4. **Biblioteca Gráfica:** Arquivo `jfreechart-*.jar` incluído no projeto.

---

## Configuração

### Banco de Dados
1. Crie um banco de dados no MySQL.
2. Execute o script SQL abaixo para criar o banco e as tabelas necessárias:
   ```sql
   CREATE DATABASE controle_consumo_energetico;

   USE controle_consumo_energetico;

   CREATE TABLE IF NOT EXISTS usuarios (
       id INT AUTO_INCREMENT PRIMARY KEY,
       username VARCHAR(50) NOT NULL UNIQUE,
       senha VARCHAR(255) NOT NULL
   );

   CREATE TABLE IF NOT EXISTS registro_consumo (
       id INT AUTO_INCREMENT PRIMARY KEY,
       data DATE NOT NULL,
       valor FLOAT NOT NULL,
       descricao VARCHAR(255),
       usuario_id INT,
       FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
   );

   CREATE TABLE IF NOT EXISTS meta_consumo (
       id INT AUTO_INCREMENT PRIMARY KEY,
       usuario_id INT,
       valor_meta FLOAT NOT NULL,
       tipo VARCHAR(20),
       FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
   );
   ```

3. Atualize o arquivo `config.properties` com as informações do seu banco de dados:
   ```
   db.url=jdbc:mysql://localhost:3306/controle_consumo_energetico
   db.user=seu_usuario
   db.password=sua_senha
   ```

---

### Execução
1. Certifique-se de que o banco de dados está configurado e rodando.
2. Compile e execute o projeto no IntelliJ IDEA ou Eclipse.
3. Para criar o arquivo `.jar`:
   - Vá para `File > Project Structure > Artifacts`.
   - Configure o artefato e clique em "Build".
4. Execute o arquivo `.jar` criado:
   ```bash
   java -jar ControleConsumoEnergetico.jar
   ```

---

## Estrutura do Projeto

- `src/` - Código fonte do projeto.
  - `ControleConsumoApp.java` - Classe principal com a interface gráfica.
  - `DatabaseConnection.java` - Classe para conexão com o banco de dados.
  - Outras classes relacionadas a registro de consumo e gerenciamento de metas.
- `config.properties` - Arquivo de configuração do banco de dados.
- `README.md` - Documentação do projeto.
- `lib/` - Bibliotecas externas (JDBC, JFreeChart).

---

## Equipe do Projeto

- **Nome:** Vinicius do Carmo Fonseca Freitas
  - **Matrícula:** RM 97599.
- **Nome:** Gustavo Cristiano Pessoa de Souza
  - **Matrícula:** RM 552093.
- **Nome:** Gustavo Medeiros Miranda da Silva
  - **Matrícula:** RM 551924.
---

## Observações

Caso tenha problemas com dependências ou execução, certifique-se de que:
1. As bibliotecas externas estão devidamente adicionadas ao projeto.
2. O banco de dados foi configurado corretamente.

---
