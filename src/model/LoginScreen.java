import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginScreen() {
        frame = new JFrame("Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Bem-vindo ao Sistema de Controle de Consumo Energético", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        frame.add(headerLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Usuário:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Senha:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton loginButton = new JButton("Entrar");
        loginButton.addActionListener(new LoginActionListener());
        buttonPanel.add(loginButton);

        JButton registerButton = new JButton("Registrar");
        registerButton.addActionListener(e -> showRegisterScreen());
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showRegisterScreen() {
        JFrame registerFrame = new JFrame("Registrar");
        registerFrame.setSize(350, 220);
        registerFrame.setLayout(new GridBagLayout());
        registerFrame.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel newUsernameLabel = new JLabel("Novo Usuário:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        registerFrame.add(newUsernameLabel, gbc);

        JTextField newUsernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        registerFrame.add(newUsernameField, gbc);

        JLabel newPasswordLabel = new JLabel("Nova Senha:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        registerFrame.add(newPasswordLabel, gbc);

        JPasswordField newPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        registerFrame.add(newPasswordField, gbc);

        JButton createAccountButton = new JButton("Criar Conta");
        createAccountButton.addActionListener(e -> {
            String newUsername = newUsernameField.getText();
            String newPassword = new String(newPasswordField.getPassword());
            if (registerUser(newUsername, hashPassword(newPassword))) {
                JOptionPane.showMessageDialog(registerFrame, "Conta criada com sucesso!");
                registerFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(registerFrame, "Erro ao criar conta. Tente um nome de usuário diferente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        registerFrame.add(createAccountButton, gbc);

        registerFrame.setVisible(true);
    }

    private boolean registerUser(String username, String hashedPassword) {
        String query = "INSERT INTO usuarios (username, senha) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            System.out.println("Erro ao registrar usuário: " + ex.getMessage());
            return false;
        }
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateUser(username, hashPassword(password))) {
                JOptionPane.showMessageDialog(frame, "Login bem-sucedido!");
                frame.dispose(); // Fecha a tela de login

                // Exibe a SplashScreen e inicia a aplicação principal após 2 segundos
                SplashScreen splashScreen = new SplashScreen();
                Timer timer = new Timer(2000, event -> {
                    splashScreen.close(); // Fecha a splash screen
                    new ControleConsumoApp(); // Inicia a aplicação principal
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                JOptionPane.showMessageDialog(frame, "Usuário ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean authenticateUser(String username, String hashedPassword) {
        String query = "SELECT * FROM usuarios WHERE username = ? AND senha = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Retorna true se o usuário for encontrado

        } catch (SQLException ex) {
            System.out.println("Erro de autenticação: " + ex.getMessage());
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao calcular hash da senha", e);
        }
    }

    public static void main(String[] args) {
        // Inicializa a conexão com o banco de dados se necessário
        DatabaseConnection.initializeDatabase();
        // Abre a tela de login
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
