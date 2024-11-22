import java.sql.Connection;
import java.sql.Date; // Usado exclusivamente para operações com o banco de dados
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar; // Para manipulações gerais de data
import java.util.Locale;

public class ControleConsumo {
    private List<RegistroConsumo> consumoList = new ArrayList<>();
    private MetaConsumo meta;

    // Método para registrar consumo
    public void registrarConsumo(java.util.Date data, float valor, String descricao, int usuarioId) {
        String sql = "INSERT INTO registro_consumo (data, valor, descricao, usuario_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new Date(data.getTime())); // Converter java.util.Date para java.sql.Date
            stmt.setFloat(2, valor);
            stmt.setString(3, descricao);
            stmt.setInt(4, usuarioId);

            stmt.executeUpdate();
            System.out.println("Consumo registrado com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao registrar consumo: " + e.getMessage());
        }
    }

    // Método para carregar dados do banco
    public void carregarDados(int usuarioId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM registro_consumo WHERE usuario_id = ?")) {

            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                consumoList.clear();
                while (rs.next()) {
                    Date data = new Date(rs.getDate("data").getTime());
                    float valor = rs.getFloat("valor");
                    String descricao = rs.getString("descricao");
                    consumoList.add(new RegistroConsumo(data, valor, descricao));
                }
            }

            System.out.println("Dados carregados com sucesso para o usuário ID: " + usuarioId);
        } catch (SQLException e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    // Método para calcular o consumo total do mês atual
    public float calcularConsumoTotalMesAtual() {
        float total = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(valor) AS total FROM registro_consumo WHERE MONTH(data) = MONTH(CURDATE()) AND YEAR(data) = YEAR(CURDATE())")) {

            if (rs.next()) {
                total = rs.getFloat("total");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao calcular consumo total do mês atual: " + e.getMessage());
        }

        return total;
    }

    // Método para calcular o consumo mensal
    public Map<String, Float> calcularConsumoMensal() {
        Map<String, Float> consumoMensal = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DATE_FORMAT(data, '%m/%Y') AS mesAno, SUM(valor) AS total FROM registro_consumo GROUP BY mesAno")) {

            while (rs.next()) {
                consumoMensal.put(rs.getString("mesAno"), rs.getFloat("total"));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao calcular consumo mensal: " + e.getMessage());
        }

        return consumoMensal;
    }

    // Método para calcular o consumo médio diário
    public float calcularConsumoMedioDiario() {
        float total = 0;
        int dias = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT data, SUM(valor) AS total FROM registro_consumo GROUP BY data")) {

            while (rs.next()) {
                total += rs.getFloat("total");
                dias++;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao calcular consumo médio diário: " + e.getMessage());
        }

        return (dias > 0) ? total / dias : 0;
    }

    // Método para comparar o consumo do mês anterior com o mês atual
    public float compararMesAnterior() {
        float totalMesAtual = calcularConsumoTotalMesAtual();
        float totalMesAnterior = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(valor) AS total FROM registro_consumo WHERE MONTH(data) = MONTH(CURDATE()) - 1 AND YEAR(data) = YEAR(CURDATE())")) {

            if (rs.next()) {
                totalMesAnterior = rs.getFloat("total");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao comparar consumo com o mês anterior: " + e.getMessage());
        }

        return totalMesAtual - totalMesAnterior;
    }

    // Método para obter o consumo diário
    public Map<java.util.Date, Float> getConsumoDiario() {
        Map<java.util.Date, Float> consumoDiario = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT data, SUM(valor) AS total FROM registro_consumo GROUP BY data")) {

            while (rs.next()) {
                java.util.Date data = new java.util.Date(rs.getDate("data").getTime()); // Converter java.sql.Date para java.util.Date
                float total = rs.getFloat("total");
                consumoDiario.put(data, total);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao obter consumo diário: " + e.getMessage());
        }

        return consumoDiario;
    }

    // Método para definir meta de consumo
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

    // Método para obter a meta atual
    public MetaConsumo getMeta() {
        return this.meta;
    }

    // Método para obter a lista de consumo
    public List<RegistroConsumo> getConsumoList() {
        return consumoList;
    }
}