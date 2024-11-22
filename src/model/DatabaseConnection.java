import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // URL para conexão com o banco de dados
    private static final String URL = "jdbc:mysql://localhost:3306/controle_consumo_energetico";
    // Credenciais do banco de dados
    private static final String USER = "controle_usuario"; // Usuário criado no script SQL
    private static final String PASSWORD = "senha123"; // Senha definida no script SQL

    // Variável de conexão (singleton)
    private static Connection connection;

    /**
     * Obtém a conexão com o banco de dados.
     *
     * @return Objeto Connection
     * @throws SQLException Se ocorrer um erro durante a conexão
     */
    public static Connection getConnection() throws SQLException {
        // Cria uma nova conexão se ela não existir ou estiver fechada
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    /**
     * Inicializa o banco de dados, verificando e criando tabelas se necessário.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "senha VARCHAR(255) NOT NULL)";

            stmt.executeUpdate(sql);
            System.out.println("Tabela 'usuarios' verificada/inicializada com sucesso.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao inicializar o banco de dados: " + e.getMessage());
        }
    }
}