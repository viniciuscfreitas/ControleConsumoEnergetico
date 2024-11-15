import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    public SplashScreen() {
        JLabel label = new JLabel("Carregando...", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setVisible(true);

        // Define um Timer para fechar a splash screen após 2 segundos
        Timer timer = new Timer(2000, e -> close());
        timer.setRepeats(false);
        timer.start();
    }

    public void close() {
        dispose();
    }
}