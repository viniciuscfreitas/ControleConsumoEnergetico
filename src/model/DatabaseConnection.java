import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/controle_consumo";
    private static final String USER = "root"; // Substitua pelo seu usuário do MySQL
    private static final String PASSWORD = "vini1234"; // Substitua pela sua senha do MySQL

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

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