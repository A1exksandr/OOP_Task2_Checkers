package checkers;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private GamePanel gamePanel;

    public MainWindow() {
        initializeWindow();
        initializeComponents();
        setupLayout();
    }

    private void initializeWindow() {
        setTitle("Шашки");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null); // Центрирование окна
    }

    private void initializeComponents() {
        gamePanel = new GamePanel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);

        pack(); // Автоматический подбор размера
    }

    public void startNewGame() {
        gamePanel.startNewGame();
    }
}
