import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

public class ControleConsumo {
    private List<RegistroConsumo> consumoList;
    private MetaConsumo meta;
    private boolean dadosCarregados = false;
    private static final String INSERT_CONSUMO_SQL = "INSERT INTO consumo (data, valor, descricao) VALUES (?, ?, ?)";
    private static final String SELECT_CONSUMO_SQL = "SELECT * FROM consumo";
    private static final String SELECT_TOTAL_MENSAL_SQL = "SELECT DATE_FORMAT(data, '%m/%Y') AS mesAno, SUM(valor) AS total FROM consumo GROUP BY mesAno";
    private static final String SELECT_TOTAL_DIARIO_SQL = "SELECT data, SUM(valor) AS total FROM consumo GROUP BY data";
    private static final String SELECT_CONSUMO_MES_ATUAL = "SELECT SUM(valor) FROM consumo WHERE MONTH(data) = MONTH(CURDATE()) AND YEAR(data) = YEAR(CURDATE())";
    private static final String SELECT_CONSUMO_MES_ANTERIOR = "SELECT SUM(valor) FROM consumo WHERE MONTH(data) = MONTH(CURDATE()) - 1 AND YEAR(data) = YEAR(CURDATE())";

    // Construtor
    public ControleConsumo() {
        this.consumoList = new ArrayList<>();
        this.meta = null;
        carregarDados();  // Carregar dados ao iniciar
    }

    // Método para adicionar um novo registro de consumo com descrição
    public void registrarConsumo(Date data, float valor, String descricao) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CONSUMO_SQL)) {
            preparedStatement.setDate(1, new java.sql.Date(data.getTime()));
            preparedStatement.setFloat(2, valor);
            preparedStatement.setString(3, descricao);
            preparedStatement.executeUpdate();
            System.out.println("Consumo registrado no banco de dados.");
        } catch (SQLException e) {
            System.out.println("Erro ao registrar consumo: " + e.getMessage());
        }
    }

    // Método para definir a meta de consumo
    public void definirMeta(float valorMeta, String tipo) {
        this.meta = new MetaConsumo(valorMeta, tipo);
    }

    // Método para verificar se o consumo total ultrapassou a meta
    public boolean verificarAlerta() {
        if (meta == null) {
            return false;
        }
        float totalConsumo = calcularConsumoTotalMesAtual();
        return totalConsumo > meta.getValorMeta();
    }

    // Método para obter a lista de consumo (usado na interface gráfica para exibir o histórico)
    public List<RegistroConsumo> getConsumoList() {
        if (!dadosCarregados) {
            carregarDados();
        }
        return consumoList;
    }

    // Método para carregar dados do banco de dados
    public void carregarDados() {
        if (dadosCarregados) {
            return;
        }
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_CONSUMO_SQL)) {
            consumoList.clear();
            while (resultSet.next()) {
                Date data = resultSet.getDate("data");
                float valor = resultSet.getFloat("valor");
                String descricao = resultSet.getString("descricao");
                consumoList.add(new RegistroConsumo(data, valor, descricao));
            }
            dadosCarregados = true;  // Marca como carregado
            System.out.println("Dados carregados do banco de dados.");
        } catch (SQLException e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    // Método para calcular o consumo total por mês
    public Map<String, Float> calcularConsumoMensal() {
        Map<String, Float> consumoMensal = new HashMap<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_TOTAL_MENSAL_SQL)) {
            while (resultSet.next()) {
                String mesAno = resultSet.getString("mesAno");
                float total = resultSet.getFloat("total");
                consumoMensal.put(mesAno, total);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao calcular consumo mensal: " + e.getMessage());
        }
        return consumoMensal;
    }

    // Método para calcular o consumo médio diário
    public float calcularConsumoMedioDiario() {
        if (consumoList.isEmpty()) return 0;
        float totalConsumo = 0;
        for (RegistroConsumo consumo : consumoList) {
            totalConsumo += consumo.getValor();
        }
        return totalConsumo / consumoList.size();
    }

    // Método para calcular o consumo total do mês atual
    public float calcularConsumoTotalMesAtual() {
        float totalMes = 0;
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_CONSUMO_MES_ATUAL)) {
            if (resultSet.next()) {
                totalMes = resultSet.getFloat(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao calcular consumo do mês atual: " + e.getMessage());
        }
        return totalMes;
    }

    // Método para comparar o consumo do mês anterior com o mês atual
    public float compararMesAnterior() {
        float totalMesAtual = calcularConsumoTotalMesAtual();
        float totalMesAnterior = 0;
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_CONSUMO_MES_ANTERIOR)) {
            if (resultSet.next()) {
                totalMesAnterior = resultSet.getFloat(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao comparar com o mês anterior: " + e.getMessage());
        }
        return totalMesAtual - totalMesAnterior;
    }

    // Método para obter o consumo diário
    public Map<Date, Float> getConsumoDiario() {
        Map<Date, Float> consumoDiario = new HashMap<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_TOTAL_DIARIO_SQL)) {
            while (resultSet.next()) {
                Date data = resultSet.getDate("data");
                float total = resultSet.getFloat("total");
                consumoDiario.put(data, total);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao obter consumo diário: " + e.getMessage());
        }
        return consumoDiario;
    }

    // Método para obter a meta atual
    public MetaConsumo getMeta() {
        return this.meta;
    }
}