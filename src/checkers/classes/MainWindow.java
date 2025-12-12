package checkers.classes;

import checkers.game.GamePanel;
import checkers.game.GameSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame {
    private GamePanel gamePanel;
    private GameSettings settings = new GameSettings();

    public MainWindow() {
        Messages.setLocale(settings.getLocale());
        initializeWindow();
        initializeComponents();
        setupMenu();
        setupLayout();
    }
    private void updateMenuTexts() {
        JMenuBar menuBar = getJMenuBar();
        JMenu gameMenu = menuBar.getMenu(0);
        gameMenu.setText(Messages.get("button.settings"));
        gameMenu.getItem(0).setText(Messages.get("button.settings"));
        gameMenu.getItem(1).setText(Messages.get("button.new_game"));
    }

    private void updateWindowTitle() {
        setTitle(Messages.get("game.title"));
    }

    private void initializeWindow() {
        setTitle(Messages.get("game.title")); // ← динамически
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        gamePanel = new GamePanel(settings);
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu(Messages.get("menu.game")); // ← динамически

        JMenuItem settingsItem = new JMenuItem(Messages.get("button.settings"));
        settingsItem.addActionListener(this::openSettings);
        gameMenu.add(settingsItem);

        JMenuItem newGameItem = new JMenuItem(Messages.get("button.new_game"));
        newGameItem.addActionListener(e -> startNewGame());
        gameMenu.add(newGameItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    private void openSettings(ActionEvent e) {
        SettingsDialog dialog = new SettingsDialog(this, settings, gamePanel.getGame());
        dialog.setVisible(true);
        Messages.setLocale(settings.getLocale());
        // Обновляем UI главного окна
        setTitle(Messages.get("game.title")); // обновляем заголовок
        // Обновляем тексты в меню
        JMenuBar menuBar = getJMenuBar();
        if (menuBar != null && menuBar.getMenuCount() > 0) {
            JMenu gameMenu = menuBar.getMenu(0);
            gameMenu.setText(Messages.get("menu.game"));
            if (gameMenu.getItemCount() >= 2) {
                gameMenu.getItem(0).setText(Messages.get("button.settings"));
                gameMenu.getItem(1).setText(Messages.get("button.new_game"));
            }
        }

        // Пересоздаём игровую панель с новыми настройками
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