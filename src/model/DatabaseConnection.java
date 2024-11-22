import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    // Variáveis para conexão com o banco de dados
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // Arquivo de configuração
    private static final String CONFIG_FILE = "config.properties";

    // Variável de conexão (singleton)
    private static Connection connection;

    static {
        // Carrega as configurações do arquivo config.properties
        try {
            Properties props = new Properties();
            FileInputStream input = new FileInputStream(CONFIG_FILE);
            props.load(input);

            String host = props.getProperty("db.host", "localhost");
            String port = props.getProperty("db.port", "3306");
            String dbName = props.getProperty("db.name", "controle_consumo_energetico");
            USER = props.getProperty("db.user", "controle_usuario"); // Valor padrão
            PASSWORD = props.getProperty("db.password", "senha123"); // Valor padrão

            // Monta a URL com os dados do arquivo de configuração
            URL = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false";
            System.out.println("Configurações carregadas com sucesso do arquivo: " + CONFIG_FILE);

        } catch (IOException e) {
            System.out.println("Erro ao carregar o arquivo de configuração: " + e.getMessage());
            // Valores padrão em caso de erro
            URL = "jdbc:mysql://localhost:3306/controle_consumo_energetico";
            USER = "controle_usuario";
            PASSWORD = "senha123";
        }
    }

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