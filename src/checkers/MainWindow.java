package checkers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame {
    private GamePanel gamePanel;
    private GameSettings settings = new GameSettings();

    public MainWindow() {
        initializeWindow();
        initializeComponents();
        setupMenu();
        setupLayout();
    }

    private void initializeWindow() {
        setTitle("Шашки");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        gamePanel = new GamePanel(settings);
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Игра");
        JMenuItem settingsItem = new JMenuItem("Настройки");
        settingsItem.addActionListener(this::openSettings);
        gameMenu.add(settingsItem);

        JMenuItem newGameItem = new JMenuItem("Новая игра");
        newGameItem.addActionListener(e -> startNewGame());
        gameMenu.add(newGameItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    private void openSettings(ActionEvent e) {
        SettingsDialog dialog = new SettingsDialog(this, settings, gamePanel.getGame());
        dialog.setVisible(true);
        // Пересоздаём панель с новыми настройками
        getContentPane().remove(gamePanel);
        gamePanel = new GamePanel(settings);
        getContentPane().add(gamePanel, BorderLayout.CENTER);
        pack();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        pack();
    }

    public void startNewGame() {
        gamePanel.startNewGame();
    }
}