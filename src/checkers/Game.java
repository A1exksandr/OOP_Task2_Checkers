package checkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private Player[] players;
    private Player currentPlayer;
    private GameState gameState;
    private Checker activeCapturingChecker = null;
    private List<Checker> capturedThisTurn = new ArrayList<>();

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
    public Checker getActiveCapturingChecker() { return activeCapturingChecker; }

    // Основной метод получения допустимых ходов
    public List<Cell> getValidMoves(Checker checker) {
        if (activeCapturingChecker != null) {
            if (checker == activeCapturingChecker) {
                return getCaptureMoves(checker, capturedThisTurn);
            } else {
                return new ArrayList<>(); // нельзя ходить другими шашками
            }
        }

        List<Cell> validMoves = new ArrayList<>();
        if (checker.getColor() != currentPlayer.getColor()) {
            return validMoves;
        }

        boolean mustCapture = mustCaptureExists();
        if (mustCapture) {
            validMoves.addAll(getCaptureMoves(checker, new ArrayList<>()));
        } else {
            validMoves.addAll(getNormalMoves(checker));
        }
        return validMoves;
    }

    private boolean mustCaptureExists() {
        for (Checker checker : board.getCheckersByColor(currentPlayer.getColor())) {
            if (!getCaptureMoves(checker, new ArrayList<>()).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<Cell> getNormalMoves(Checker checker) {
        List<Cell> moves = new ArrayList<>();
        int x = checker.getCell().getX();
        int y = checker.getCell().getY();
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

    private void findCaptureMoves(Checker checker, Cell currentCell, List<Cell> captureMoves, List<Checker> capturedCheckers) {
        int x = currentCell.getX();
        int y = currentCell.getY();
        int[][] directions = getDirections(checker);
        for (int[] dir : directions) {
            int enemyX = x + dir[0];
            int enemyY = y + dir[1];
            Cell enemyCell = board.getCell(enemyX, enemyY);
            int targetX = x + 2 * dir[0];
            int targetY = y + 2 * dir[1];
            Cell targetCell = board.getCell(targetX, targetY);
            if (enemyCell != null && targetCell != null &&
                    enemyCell.hasChecker() &&
                    enemyCell.getChecker().getColor() != checker.getColor() &&
                    targetCell.isEmpty() &&
                    !capturedCheckers.contains(enemyCell.getChecker())) {

                captureMoves.add(targetCell);
                List<Checker> newCaptured = new ArrayList<>(capturedCheckers);
                newCaptured.add(enemyCell.getChecker());
                findCaptureMoves(checker, targetCell, captureMoves, newCaptured);
            }
        }
    }

    private List<Cell> getCaptureMoves(Checker checker) {
        return getCaptureMoves(checker, new ArrayList<>());
    }

    private List<Cell> getCaptureMoves(Checker checker, List<Checker> alreadyCaptured) {
        List<Cell> captureMoves = new ArrayList<>();
        findCaptureMoves(checker, checker.getCell(), captureMoves, alreadyCaptured);
        return captureMoves;
    }

    private int[][] getDirections(Checker checker) {
        if (checker.getType() == CheckerType.KING) {
            return new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        } else {
            if (checker.isWhite()) {
                return new int[][]{{1, -1}, {-1, -1}};
            } else {
                return new int[][]{{1, 1}, {-1, 1}};
            }
        }
    }

    public void makeMove(Checker checker, Cell targetCell) {
        if (activeCapturingChecker != null && activeCapturingChecker != checker) {
            return; // нельзя менять шашку во время цепочки
        }

        Cell fromCell = checker.getCell();
        board.moveChecker(checker, targetCell);

        if (isCaptureMove(fromCell, targetCell)) {
            Cell capturedCell = getCapturedCell(fromCell, targetCell);
            if (capturedCell != null && capturedCell.hasChecker()) {
                Checker capturedChecker = capturedCell.getChecker();
                capturedThisTurn.add(capturedChecker);
            }
        }

        checkPromotion(checker);

        if (isCaptureMove(fromCell, targetCell) && canContinueCapture(checker, capturedThisTurn)) {
            activeCapturingChecker = checker;
            return;
        }

        // Завершение хода: удаляем все съеденные шашки
        for (Checker c : capturedThisTurn) {
            board.removeChecker(c);
        }
        capturedThisTurn.clear();
        activeCapturingChecker = null;

        switchPlayer();
        checkGameOver();
    }

    private boolean isCaptureMove(Cell from, Cell to) {
        return Math.abs(from.getX() - to.getX()) == 2 &&
                Math.abs(from.getY() - to.getY()) == 2;
    }

    private Cell getCapturedCell(Cell from, Cell to) {
        int capturedX = (from.getX() + to.getX()) / 2;
        int capturedY = (from.getY() + to.getY()) / 2;
        return board.getCell(capturedX, capturedY);
    }

    private void checkPromotion(Checker checker) {
        int y = checker.getCell().getY();
        if (checker.getType() == CheckerType.REGULAR) {
            if ((checker.isWhite() && y == 0) || (checker.isBlack() && y == 7)) {
                board.promoteToKing(checker);
            }
        }
    }

    private boolean canContinueCapture(Checker checker, List<Checker> alreadyCaptured) {
        return !getCaptureMoves(checker, alreadyCaptured).isEmpty();
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == players[0]) ? players[1] : players[0];
    }

    private void checkGameOver() {
        List<Checker> currentPlayerCheckers = board.getCheckersByColor(currentPlayer.getColor());
        if (currentPlayerCheckers.isEmpty()) {
            gameState = currentPlayer.getColor() == Color.WHITE ? GameState.BLACK_WIN : GameState.WHITE_WIN;
            return;
        }
        if (!canPlayerMove(currentPlayer)) {
            gameState = currentPlayer.getColor() == Color.WHITE ? GameState.BLACK_WIN : GameState.WHITE_WIN;
        }
    }

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