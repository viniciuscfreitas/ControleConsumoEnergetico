-- Cria o banco de dados para o projeto
CREATE DATABASE IF NOT EXISTS controle_consumo_energetico;

-- Usa o banco de dados criado
USE controle_consumo_energetico;

-- Cria a tabela para registrar o consumo de energia
CREATE TABLE IF NOT EXISTS registro_consumo (
                                                id INT AUTO_INCREMENT PRIMARY KEY,
                                                data DATE NOT NULL,
                                                valor FLOAT NOT NULL,
                                                descricao VARCHAR(255)
    );

-- Cria a tabela para registrar as metas de consumo
CREATE TABLE IF NOT EXISTS meta_consumo (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            valor_meta FLOAT NOT NULL,
                                            tipo_meta VARCHAR(50),
    data_definicao DATE NOT NULL
    );

-- Cria um usuário específico para o projeto com permissões restritas
CREATE USER IF NOT EXISTS 'controle_usuario'@'localhost' IDENTIFIED BY 'senha123';

-- Concede permissões para o usuário no banco de dados controle_consumo_energetico
GRANT ALL PRIVILEGES ON controle_consumo_energetico.* TO 'controle_usuario'@'localhost';

-- Aplica as permissões
FLUSH PRIVILEGES;