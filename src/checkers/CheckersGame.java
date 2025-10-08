package checkers;

import javax.swing.*;

public class CheckersGame {
    public static void main(String[] args) {
        // Запуск в EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
    }
}