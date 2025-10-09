// MainWindow.java (обновлённый)
package checkers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Locale;

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
        updateTitle();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void updateTitle() {
        setTitle(Messages.get("game.title"));
    }

    private void initializeComponents() {
        gamePanel = new GamePanel();
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu(Messages.get("button.settings"));
        JMenuItem settingsItem = new JMenuItem(Messages.get("button.settings"));
        settingsItem.addActionListener(this::openSettingsDialog);
        gameMenu.add(settingsItem);

        JMenuItem newGameItem = new JMenuItem(Messages.get("button.new_game"));
        newGameItem.addActionListener(e -> startNewGame());
        gameMenu.add(newGameItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    private void openSettingsDialog(ActionEvent e) {
        SettingsDialog dialog = new SettingsDialog(this, settings);
        dialog.setVisible(true);
        // После закрытия — обновляем язык и заголовок
        Messages.setLocale(settings.getLocale());
        updateTitle();
        // Если хочешь — можно перезапустить игру при смене правил
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