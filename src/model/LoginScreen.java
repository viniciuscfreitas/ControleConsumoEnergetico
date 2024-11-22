import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen {
    private JFrame frame;
    private JTextField campoUsuario;
    private JPasswordField campoSenha;
    private JButton botaoLogin, botaoRegistrar;

    public LoginScreen() {
        criarInterface();
    }

    private void criarInterface() {
        frame = new JFrame("Login - Controle de Consumo Energético");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelUsuario = new JLabel("Usuário:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(labelUsuario, gbc);

        campoUsuario = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(campoUsuario, gbc);

        JLabel labelSenha = new JLabel("Senha:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(labelSenha, gbc);

        campoSenha = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(campoSenha, gbc);

        botaoLogin = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.add(botaoLogin, gbc);

        botaoRegistrar = new JButton("Registrar");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        frame.add(botaoRegistrar, gbc);

        adicionarEventos();

        frame.setVisible(true);
    }

    private void adicionarEventos() {
        botaoLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = campoUsuario.getText().trim();
                String senha = new String(campoSenha.getPassword()).trim();

                if (nome.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int usuarioId = validarCredenciais(nome, senha); // Verifica o login e retorna o ID do usuário
                if (usuarioId != -1) { // Login bem-sucedido
                    JOptionPane.showMessageDialog(null, "Login realizado com sucesso!");
                    SwingUtilities.invokeLater(() -> new ControleConsumoApp(usuarioId)); // Passa o ID do usuário para a próxima tela
                    frame.dispose(); // Fecha a tela de login
                } else {
                    JOptionPane.showMessageDialog(null, "Usuário ou senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        botaoRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = campoUsuario.getText().trim();
                String senha = new String(campoSenha.getPassword()).trim();

                if (nome.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (registrarUsuario(nome, senha)) {
                    JOptionPane.showMessageDialog(null, "Usuário registrado com sucesso! Agora você pode fazer login.");
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao registrar usuário. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private int validarCredenciais(String nome, String senha) {
        String query = "SELECT id FROM usuarios WHERE nome = ? AND senha = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nome);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); // Retorna o ID do usuário se o login for bem-sucedido
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao validar credenciais: " + e.getMessage());
        }
        return -1; // Retorna -1 se o login falhar
    }

    private boolean registrarUsuario(String nome, String senha) {
        String query = "INSERT INTO usuarios (nome, senha) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nome);
            stmt.setString(2, senha);
            stmt.executeUpdate();
            return true; // Registro bem-sucedido

        } catch (SQLException e) {
            System.err.println("Erro ao registrar usuário: " + e.getMessage());
        }
        return false; // Falha no registro
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}