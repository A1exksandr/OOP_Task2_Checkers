package checkers.classes;

import checkers.game.GamePanel;
import checkers.game.GameSettings;
import checkers.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {
    private GamePanel gamePanel;
    private GameSettings settings = new GameSettings();

    public MainWindow() {
        // Загружаем сохраненную игру (если есть)
        Game loadedGame = Game.loadGame();

        if (loadedGame != null) {
            // Используем загруженную игру
            this.settings = loadedGame.getSettings();
            Messages.setLocale(settings.getLocale());
            gamePanel = new GamePanel(settings, loadedGame);

            // Восстанавливаем размер окна
            if (loadedGame.getWindowBounds() != null) {
                setBounds(loadedGame.getWindowBounds());
            } else {
                setSize(600, 600);
                setLocationRelativeTo(null);
            }
        } else {
            // Создаем новую игру
            Messages.setLocale(settings.getLocale());
            gamePanel = new GamePanel(settings);
            setSize(600, 600);
            setLocationRelativeTo(null);
        }

        initializeWindow();
        setupMenu();
        setupLayout();

        // Слушатель для сохранения при закрытии
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveState();
            }
        });
    }

    private void saveState() {
        Game game = gamePanel.getGame();
        game.setWindowBounds(getBounds());
        game.saveGame();
    }

    private void updateMenuTexts() {
        JMenuBar menuBar = getJMenuBar();
        if (menuBar != null && menuBar.getMenuCount() > 0) {
            JMenu gameMenu = menuBar.getMenu(0);
            gameMenu.setText(Messages.get("menu.game"));

            if (gameMenu.getItemCount() >= 3) {
                gameMenu.getItem(0).setText(Messages.get("button.settings"));
                gameMenu.getItem(1).setText(Messages.get("button.new_game"));
                gameMenu.getItem(2).setText(Messages.get("button.load_game"));

                // Обновляем состояние кнопки загрузки
                gameMenu.getItem(2).setEnabled(Game.saveExists());
            }
        }
    }

    private void updateWindowTitle() {
        setTitle(Messages.get("game.title"));
    }

    private void initializeWindow() {
        setTitle(Messages.get("game.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu(Messages.get("menu.game"));

        JMenuItem settingsItem = new JMenuItem(Messages.get("button.settings"));
        settingsItem.addActionListener(this::openSettings);
        gameMenu.add(settingsItem);

        JMenuItem newGameItem = new JMenuItem(Messages.get("button.new_game"));
        newGameItem.addActionListener(e -> startNewGame());
        gameMenu.add(newGameItem);

        // Кнопка загрузки игры
        JMenuItem loadGameItem = new JMenuItem(Messages.get("button.load_game"));
        loadGameItem.addActionListener(e -> loadGame());
        loadGameItem.setEnabled(Game.saveExists());
        gameMenu.add(loadGameItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    private void loadGame() {
        Game loadedGame = Game.loadGame();
        if (loadedGame != null) {
            this.settings = loadedGame.getSettings();
            Messages.setLocale(settings.getLocale());

            // Обновляем UI
            updateWindowTitle();
            updateMenuTexts();

            // ВАЖНО: обновляем существующую панель вместо создания новой
            gamePanel.updateGame(loadedGame);

            // Перерисовываем всю панель
            gamePanel.revalidate();
            gamePanel.repaint();

            // Восстанавливаем размер окна
            if (loadedGame.getWindowBounds() != null) {
                setBounds(loadedGame.getWindowBounds());
            } else {
                pack();
            }

            revalidate();
            repaint();

            JOptionPane.showMessageDialog(this,
                    "Игра загружена успешно!",
                    "Информация",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Не удалось загрузить игру.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSettings(ActionEvent e) {
        SettingsDialog dialog = new SettingsDialog(this, settings, gamePanel.getGame());
        dialog.setVisible(true);

        // После закрытия диалога проверяем, изменились ли настройки
        Messages.setLocale(settings.getLocale());

        // Обновляем UI главного окна
        updateWindowTitle();
        updateMenuTexts();

        // ВАЖНО: обновляем панель с новыми настройками
        Game newGame = new Game(settings);
        gamePanel.updateGame(newGame);

        // Перерисовываем
        pack();
        revalidate();
        repaint();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        pack();
    }

    public void startNewGame() {
        // Удаляем сохранение
        gamePanel.getGame().deleteSave();

        // Обновляем состояние кнопки загрузки в меню
        updateMenuTexts();

        // Создаем новую игру
        Game newGame = new Game(settings);
        gamePanel.updateGame(newGame);

        // Перерисовываем
        pack();
        revalidate();
        repaint();
    }
}