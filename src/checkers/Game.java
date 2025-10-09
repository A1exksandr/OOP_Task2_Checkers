package checkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private Player[] players;
    private Player currentPlayer;
    private GameState gameState;

    public Game() {
        this.board = new Board();
        this.players = new Player[] {
                new Player(Color.WHITE, PlayerType.HUMAN, "Белые"),
                new Player(Color.BLACK, PlayerType.HUMAN, "Черные")
        };
        this.currentPlayer = players[0]; // Белые начинают
        this.gameState = GameState.IN_PROGRESS;
    }

    // Геттеры
    public Board getBoard() { return board; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public GameState getGameState() { return gameState; }

    // Основной метод получения допустимых ходов
    public List<Cell> getValidMoves(Checker checker) {
        List<Cell> validMoves = new ArrayList<>();

        if (checker.getColor() != currentPlayer.getColor()) {
            return validMoves; // Не своя шашка - нет ходов
        }

        // Проверяем, есть ли обязательные взятия
        boolean mustCapture = mustCaptureExists();

        if (mustCapture) {
            validMoves.addAll(getCaptureMoves(checker));
        } else {
            validMoves.addAll(getNormalMoves(checker));
        }

        return validMoves;
    }

    // Проверка наличия обязательных взятий у любого игрока
    private boolean mustCaptureExists() {
        for (Checker checker : board.getCheckersByColor(currentPlayer.getColor())) {
            if (!getCaptureMoves(checker).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Получение обычных ходов
    private List<Cell> getNormalMoves(Checker checker) {
        List<Cell> moves = new ArrayList<>();
        int x = checker.getCell().getX();
        int y = checker.getCell().getY();

        // Направления движения в зависимости от цвета и типа шашки
        int[][] directions = getDirections(checker);

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            Cell targetCell = board.getCell(newX, newY);
            if (targetCell != null && targetCell.isEmpty()) {
                moves.add(targetCell);
            }
        }

        return moves;
    }

    // Рекурсивный поиск взятий (для множественного взятия)
    private void findCaptureMoves(Checker checker, Cell currentCell, List<Cell> captureMoves, List<Checker> capturedCheckers) {
        int x = currentCell.getX();
        int y = currentCell.getY();

        int[][] directions = getDirections(checker);
        boolean foundCapture = false;

        for (int[] dir : directions) {
            // Клетка с шашкой противника
            int enemyX = x + dir[0];
            int enemyY = y + dir[1];
            Cell enemyCell = board.getCell(enemyX, enemyY);

            // Клетка за шашкой противника
            int targetX = x + 2 * dir[0];
            int targetY = y + 2 * dir[1];
            Cell targetCell = board.getCell(targetX, targetY);

            if (enemyCell != null && targetCell != null &&
                    enemyCell.hasChecker() &&
                    enemyCell.getChecker().getColor() != checker.getColor() &&
                    targetCell.isEmpty() &&
                    !capturedCheckers.contains(enemyCell.getChecker())) {

                // Добавляем ход
                captureMoves.add(targetCell);
                foundCapture = true;

                // Рекурсивно ищем продолжение взятия
                List<Checker> newCaptured = new ArrayList<>(capturedCheckers);
                newCaptured.add(enemyCell.getChecker());
                findCaptureMoves(checker, targetCell, captureMoves, newCaptured);
            }
        }
    }

    // Обновленный метод getCaptureMoves
    private List<Cell> getCaptureMoves(Checker checker) {
        List<Cell> captureMoves = new ArrayList<>();
        findCaptureMoves(checker, checker.getCell(), captureMoves, new ArrayList<>());
        return captureMoves;
    }

    // Получение направлений движения в зависимости от типа шашки
    private int[][] getDirections(Checker checker) {
        if (checker.getType() == CheckerType.KING) {
            // Дамка ходит во всех направлениях
            return new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        } else {
            // Обычная шашка - только вперед
            if (checker.isWhite()) {
                return new int[][]{{1, -1}, {-1, -1}}; // Белые вверх
            } else {
                return new int[][]{{1, 1}, {-1, 1}};   // Черные вниз
            }
        }
    }

    // Основной метод выполнения хода
    public void makeMove(Checker checker, Cell targetCell) {
        Cell fromCell = checker.getCell();

        // Перемещаем шашку
        board.moveChecker(checker, targetCell);

        // Проверяем взятие
        if (isCaptureMove(fromCell, targetCell)) {
            Cell capturedCell = getCapturedCell(fromCell, targetCell);
            if (capturedCell != null && capturedCell.hasChecker()) {
                board.removeChecker(capturedCell.getChecker());
            }
        }

        // Проверяем превращение в дамку
        checkPromotion(checker);

        // Проверяем возможность продолжения взятия
        if (isCaptureMove(fromCell, targetCell) && canContinueCapture(checker)) {
            // Ход продолжается тем же игроком - не переключаем
            return;
        }

        // Переключаем ход
        switchPlayer();

        // Проверяем конец игры
        checkGameOver();
    }

    // Проверка, является ли ход взятием
    private boolean isCaptureMove(Cell from, Cell to) {
        return Math.abs(from.getX() - to.getX()) == 2 &&
                Math.abs(from.getY() - to.getY()) == 2;
    }

    // Получение сбитой шашки
    private Cell getCapturedCell(Cell from, Cell to) {
        int capturedX = (from.getX() + to.getX()) / 2;
        int capturedY = (from.getY() + to.getY()) / 2;
        return board.getCell(capturedX, capturedY);
    }

    // Проверка превращения в дамку
    private void checkPromotion(Checker checker) {
        int y = checker.getCell().getY();
        if (checker.getType() == CheckerType.REGULAR) {
            if ((checker.isWhite() && y == 0) || (checker.isBlack() && y == 7)) {
                board.promoteToKing(checker);
            }
        }
    }

    // Проверка возможности продолжения взятия
    private boolean canContinueCapture(Checker checker) {
        return !getCaptureMoves(checker).isEmpty();
    }

    // Переключение активного игрока
    private void switchPlayer() {
        currentPlayer = (currentPlayer == players[0]) ? players[1] : players[0];
    }

    // Проверка окончания игры
    private void checkGameOver() {
        // Проверяем, остались ли шашки у игрока
        List<Checker> currentPlayerCheckers = board.getCheckersByColor(currentPlayer.getColor());
        if (currentPlayerCheckers.isEmpty()) {
            gameState = currentPlayer.getColor() == Color.WHITE ? GameState.BLACK_WIN : GameState.WHITE_WIN;
            return;
        }

        // Проверяем, может ли игрок сделать ход
        if (!canPlayerMove(currentPlayer)) {
            gameState = currentPlayer.getColor() == Color.WHITE ? GameState.BLACK_WIN : GameState.WHITE_WIN;
        }
    }

    // Проверка возможности хода для игрока
    private boolean canPlayerMove(Player player) {
        for (Checker checker : board.getCheckersByColor(player.getColor())) {
            if (!getValidMoves(checker).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return gameState != GameState.IN_PROGRESS;
    }
}