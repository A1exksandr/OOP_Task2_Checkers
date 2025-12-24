package checkers.game;

import checkers.classes.*;
import checkers.enums.CheckerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class GamePanel extends JPanel {
    private Game game;
    private Board board;
    private Checker selectedChecker;
    private List<Cell> highlightedCells;
    private final GameSettings settings;

    private static final int BOARD_MARGIN = 10;
    private static final Color LIGHT_COLOR = new Color(240, 217, 181);
    private static final Color DARK_COLOR = new Color(181, 136, 99);
    private static final Color HIGHLIGHT_COLOR = new Color(100, 200, 100, 128);

    public GamePanel(GameSettings settings) {
        this.settings = settings;
        initSize();
        setBackground(Color.LIGHT_GRAY);
        this.game = new Game(settings);
        this.board = game.getBoard();
        this.highlightedCells = new java.util.ArrayList<>();
        addMouseListener(new CheckersMouseAdapter());
    }

    // Конструктор для загруженной игры
    public GamePanel(GameSettings settings, Game loadedGame) {
        this.settings = settings;
        initSize();
        setBackground(Color.LIGHT_GRAY);
        this.game = loadedGame;
        this.board = game.getBoard();
        this.highlightedCells = new java.util.ArrayList<>();
        addMouseListener(new CheckersMouseAdapter());
    }

    private void initSize() {
        setPreferredSize(new Dimension(
                8 * settings.getCellSize() + 2 * BOARD_MARGIN,
                8 * settings.getCellSize() + 2 * BOARD_MARGIN
        ));
    }

    public Game getGame() {
        return game;
    }

    public void startNewGame() {
        game = new Game(settings);
        board = game.getBoard();
        selectedChecker = null;
        highlightedCells.clear();
        repaint();
    }

    // Остальные методы без изменений...
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBoard(g2d);
        drawHighlightedCells(g2d);
        drawCheckers(g2d);
    }

    private void drawBoard(Graphics2D g2d) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Color cellColor = ((x + y) % 2 == 0) ? LIGHT_COLOR : DARK_COLOR;
                g2d.setColor(cellColor);
                g2d.fillRect(BOARD_MARGIN + x * settings.getCellSize(),
                        BOARD_MARGIN + y * settings.getCellSize(),
                        settings.getCellSize(), settings.getCellSize());
            }
        }
    }

    private void drawHighlightedCells(Graphics2D g2d) {
        g2d.setColor(HIGHLIGHT_COLOR);
        for (Cell cell : highlightedCells) {
            g2d.fillRect(BOARD_MARGIN + cell.getX() * settings.getCellSize(),
                    BOARD_MARGIN + cell.getY() * settings.getCellSize(),
                    settings.getCellSize(), settings.getCellSize());
        }
    }

    private void drawCheckers(Graphics2D g2d) {
        for (Checker checker : board.getCheckers()) {
            drawChecker(g2d, checker);
        }
    }

    private void drawChecker(Graphics2D g2d, Checker checker) {
        int size = settings.getCellSize();
        int x = BOARD_MARGIN + checker.getCell().getX() * size;
        int y = BOARD_MARGIN + checker.getCell().getY() * size;
        g2d.setColor(checker.getColor());
        g2d.fillOval(x + 5, y + 5, size - 10, size - 10);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x + 5, y + 5, size - 10, size - 10);
        if (checker.getType() == CheckerType.KING) {
            g2d.setColor(checker.isWhite() ? Color.BLACK : Color.WHITE);
            g2d.drawString("K", x + size/2 - 5, y + size/2 + 5);
        }
    }

    private class CheckersMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Cell clickedCell = getCellAt(e.getPoint());
            if (clickedCell == null) return;
            if (selectedChecker == null) {
                trySelectChecker(clickedCell);
            } else {
                tryMoveOrDeselect(clickedCell);
            }
        }
    }

    private void trySelectChecker(Cell cell) {
        if (cell.hasChecker() && cell.getChecker().getColor() == game.getCurrentPlayer().getColor()) {
            selectedChecker = cell.getChecker();
            highlightedCells = game.getValidMoves(selectedChecker);
            repaint();
        }
    }

    private void tryMoveOrDeselect(Cell clickedCell) {
        if (highlightedCells.contains(clickedCell)) {
            tryMoveSelectedChecker(clickedCell);
        } else if (clickedCell.hasChecker() &&
                clickedCell.getChecker().getColor() == game.getCurrentPlayer().getColor()) {
            selectedChecker = clickedCell.getChecker();
            highlightedCells = game.getValidMoves(selectedChecker);
            repaint();
        } else {
            deselectChecker();
        }
    }

    private void tryMoveSelectedChecker(Cell targetCell) {
        if (highlightedCells.contains(targetCell)) {
            game.makeMove(selectedChecker, targetCell);
            deselectChecker();
            if (game.isGameOver()) {
                showGameOverDialog();
            }
        }
    }

    private void deselectChecker() {
        selectedChecker = null;
        highlightedCells.clear();
        repaint();
    }

    private Cell getCellAt(Point point) {
        int x = (point.x - BOARD_MARGIN) / settings.getCellSize();
        int y = (point.y - BOARD_MARGIN) / settings.getCellSize();
        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            return board.getCell(x, y);
        }
        return null;
    }

    private void showGameOverDialog() {
        String message;
        switch (game.getGameState()) {
            case WHITE_WIN: message = "Победа белых!"; break;
            case BLACK_WIN: message = "Победа чёрных!"; break;
            default: message = "Игра завершена!";
        }
        JOptionPane.showMessageDialog(this, message);
    }
}