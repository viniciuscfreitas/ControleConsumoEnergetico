-- Tabela para armazenar usuários
CREATE TABLE usuarios (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          nome VARCHAR(50) UNIQUE NOT NULL,
                          senha VARCHAR(255) NOT NULL
);

-- Tabela para armazenar registros de consumo
CREATE TABLE registro_consumo (
                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                  data DATE NOT NULL,
                                  valor FLOAT NOT NULL,
                                  descricao VARCHAR(255) NOT NULL,
                                  usuario_id INT NOT NULL,
                                  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela para armazenar metas de consumo
CREATE TABLE meta_consumo (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              usuario_id INT NOT NULL,
                              valor_meta FLOAT NOT NULL,
                              tipo VARCHAR(50) NOT NULL,
                              FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);