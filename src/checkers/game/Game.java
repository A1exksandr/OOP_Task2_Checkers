package checkers.game;

import checkers.classes.*;
import checkers.enums.CheckerType;
import checkers.enums.GameState;
import checkers.enums.PlayerType;
import checkers.json.JsonHelper;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Game {
    private Board board;
    private Player[] players;
    private Player currentPlayer;
    private GameState gameState;
    private GameSettings settings;
    private List<MoveRecord> moveHistory = new ArrayList<>();
    private GameRules rules;
    private Rectangle windowBounds;

    private static final String SAVE_DIR = "saves";
    private static final String SAVE_FILE = "checkers_save.json";

    public Game(GameSettings settings) {
        this.settings = settings;
        this.rules = new GameRules(
                settings.getVariant() == GameSettings.Variant.RUSSIAN
                        ? GameRules.RuleType.RUSSIAN
                        : GameRules.RuleType.SPANISH
        );
        this.board = new Board();
        this.players = new Player[] {
                new Player(Color.WHITE, PlayerType.HUMAN, "Белые"),
                new Player(Color.BLACK, PlayerType.HUMAN, "Черные")
        };
        this.currentPlayer = players[0];
        this.gameState = GameState.IN_PROGRESS;
    }

    // Специальный конструктор для загрузки без создания начальных шашек
    private Game(GameSettings settings, boolean forLoading) {
        this.settings = settings;
        this.rules = new GameRules(
                settings.getVariant() == GameSettings.Variant.RUSSIAN
                        ? GameRules.RuleType.RUSSIAN
                        : GameRules.RuleType.SPANISH
        );
        // Создаем пустую доску без начальных шашек
        this.board = createEmptyBoard();
        this.players = new Player[] {
                new Player(Color.WHITE, PlayerType.HUMAN, "Белые"),
                new Player(Color.BLACK, PlayerType.HUMAN, "Черные")
        };
        this.currentPlayer = players[0];
        this.gameState = GameState.IN_PROGRESS;
    }

    // Создание пустой доски без шашек
    private Board createEmptyBoard() {
        Board emptyBoard = new Board();
        // Удаляем все шашки, созданные конструктором Board
        List<Checker> checkersToRemove = new ArrayList<>(emptyBoard.getCheckers());
        for (Checker checker : checkersToRemove) {
            Cell cell = checker.getCell();
            if (cell != null) {
                cell.removeChecker();
            }
        }
        emptyBoard.getCheckers().clear();
        return emptyBoard;
    }

    // Геттеры
    public Board getBoard() { return board; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public GameState getGameState() { return gameState; }
    public List<MoveRecord> getMoveHistory() { return new ArrayList<>(moveHistory); }
    public GameSettings getSettings() { return settings; }
    public Rectangle getWindowBounds() { return windowBounds; }

    public void setWindowBounds(Rectangle bounds) {
        this.windowBounds = bounds;
    }

    // Сохранение игры в JSON формате
    public void saveGame() {
        try {
            // Создаем директорию для сохранений
            Path dir = Paths.get(SAVE_DIR);
            Files.createDirectories(dir);
            Path file = dir.resolve(SAVE_FILE);

            // Собираем данные для сохранения
            Map<String, Object> saveData = new HashMap<>();

            // Сохраняем настройки
            Map<String, Object> settingsData = new HashMap<>();
            settingsData.put("variant", settings.getVariant().name());
            settingsData.put("locale", settings.getLocale().getLanguage());
            settingsData.put("cellSize", settings.getCellSize());
            saveData.put("settings", settingsData);

            // Сохраняем состояние игры
            saveData.put("gameState", gameState.name());
            saveData.put("currentPlayer", currentPlayer.getColor() == Color.WHITE ? "WHITE" : "BLACK");

            // Сохраняем размер окна
            if (windowBounds != null) {
                Map<String, Integer> windowData = new HashMap<>();
                windowData.put("x", windowBounds.x);
                windowData.put("y", windowBounds.y);
                windowData.put("width", windowBounds.width);
                windowData.put("height", windowBounds.height);
                saveData.put("window", windowData);
            }

            // Сохраняем шашки
            List<Map<String, Object>> checkersData = new ArrayList<>();
            System.out.println("Сохранение шашек: " + board.getCheckers().size());
            for (Checker checker : board.getCheckers()) {
                Cell cell = checker.getCell();
                Map<String, Object> checkerData = new HashMap<>();
                checkerData.put("color", checker.getColor() == Color.WHITE ? "WHITE" : "BLACK");
                checkerData.put("type", checker.getType().name());
                checkerData.put("x", cell.getX());
                checkerData.put("y", cell.getY());
                checkersData.add(checkerData);
                System.out.println("  Сохранена шашка: " + checker.getColor() + " " + checker.getType() +
                        " на [" + cell.getX() + "," + cell.getY() + "]");
            }
            saveData.put("checkers", checkersData);

            // Сохраняем историю ходов (упрощенно)
            List<String> historyData = new ArrayList<>();
            for (MoveRecord record : moveHistory) {
                historyData.add(record.toString());
            }
            saveData.put("history", historyData);

            // Конвертируем в JSON и сохраняем
            String json = JsonHelper.toJson(saveData);

            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
                writer.println(json);
            }

            System.out.println("Игра сохранена в JSON: " + file.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Ошибка сохранения JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Загрузка игры из JSON файла
    public static Game loadGame() {
        Path file = Paths.get(SAVE_DIR, SAVE_FILE);
        if (!Files.exists(file)) {
            System.out.println("Файл сохранения не найден: " + file.toAbsolutePath());
            return null;
        }

        try {
            // Читаем весь файл
            String json = new String(Files.readAllBytes(file));

            // Парсим JSON (упрощенный парсинг)
            Map<String, Object> data = parseJson(json);
            if (data == null) {
                System.err.println("Неверный формат JSON файла");
                return null;
            }

            // Восстанавливаем настройки
            GameSettings settings = new GameSettings();
            Map<String, Object> settingsData = (Map<String, Object>) data.get("settings");
            if (settingsData != null) {
                String variant = (String) settingsData.get("variant");
                settings.setVariant(GameSettings.Variant.valueOf(variant));

                String locale = (String) settingsData.get("locale");
                if ("en".equals(locale)) {
                    settings.setLocale(Locale.ENGLISH);
                } else if ("fr".equals(locale)) {
                    settings.setLocale(Locale.FRENCH);
                } else {
                    settings.setLocale(new Locale("ru", "RU"));
                }

                Number cellSize = (Number) settingsData.get("cellSize");
                settings.setCellSize(cellSize.intValue());
            }

            // Создаем игру с пустой доской
            Game game = new Game(settings, true);

            // Восстанавливаем состояние игры
            String gameStateStr = (String) data.get("gameState");
            if (gameStateStr != null) {
                game.gameState = GameState.valueOf(gameStateStr);
                System.out.println("Загружено состояние игры: " + gameStateStr);
            }

            String currentPlayerStr = (String) data.get("currentPlayer");
            if (currentPlayerStr != null) {
                game.currentPlayer = "WHITE".equals(currentPlayerStr) ? game.players[0] : game.players[1];
                System.out.println("Загружен текущий игрок: " + currentPlayerStr);
            }

            // Восстанавливаем размер окна
            Map<String, Object> windowData = (Map<String, Object>) data.get("window");
            if (windowData != null) {
                int x = ((Number) windowData.get("x")).intValue();
                int y = ((Number) windowData.get("y")).intValue();
                int width = ((Number) windowData.get("width")).intValue();
                int height = ((Number) windowData.get("height")).intValue();
                game.windowBounds = new Rectangle(x, y, width, height);
                System.out.println("Загружен размер окна: " + x + "," + y + "," + width + "," + height);
            }

            // Восстанавливаем шашки
            List<Map<String, Object>> checkersData = (List<Map<String, Object>>) data.get("checkers");
            if (checkersData != null) {
                System.out.println("Загружаем шашек: " + checkersData.size());
                for (Map<String, Object> checkerData : checkersData) {
                    String colorStr = (String) checkerData.get("color");
                    String typeStr = (String) checkerData.get("type");
                    int x = ((Number) checkerData.get("x")).intValue();
                    int y = ((Number) checkerData.get("y")).intValue();

                    Color color = "WHITE".equals(colorStr) ? Color.WHITE : Color.BLACK;
                    CheckerType type = CheckerType.valueOf(typeStr);

                    Cell cell = game.board.getCell(x, y);
                    if (cell != null) {
                        System.out.println("Создаем шашку: " + colorStr + " " + typeStr + " на [" + x + "," + y + "]");

                        // СОЗДАЕМ шашку и ДОБАВЛЯЕМ в список
                        Checker checker = new Checker(color, cell);
                        game.board.getCheckers().add(checker); // ВАЖНО: добавляем в список!

                        if (type == CheckerType.KING) {
                            checker.promoteToKing();
                        }
                    } else {
                        System.err.println("Ошибка: ячейка не найдена [" + x + "," + y + "]");
                    }
                }
            }

            // Проверяем результат
            System.out.println("Всего загружено шашек: " + game.board.getCheckers().size());

            // Проверяем состояние игры
            if (game.gameState == GameState.WHITE_WIN || game.gameState == GameState.BLACK_WIN) {
                System.out.println("Игра уже завершена: " + game.gameState);
            } else {
                // Проверяем, есть ли шашки у игроков
                int whiteCount = game.board.getCheckersByColor(Color.WHITE).size();
                int blackCount = game.board.getCheckersByColor(Color.BLACK).size();
                System.out.println("Белых шашек: " + whiteCount + ", черных: " + blackCount);

                if (whiteCount == 0 || blackCount == 0) {
                    System.out.println("Обнаружено окончание игры по количеству шашек");
                    game.checkGameOver();
                }
            }

            System.out.println("Игра загружена из JSON: " + file.toAbsolutePath());
            return game;

        } catch (Exception e) {
            System.err.println("Ошибка загрузки игры из JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Простой парсер JSON (остается без изменений)
    private static Map<String, Object> parseJson(String json) {
        // Упрощенный парсинг для нашего формата
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        json = json.substring(1, json.length() - 1).trim();

        // Разбиваем на пары ключ:значение
        String[] pairs = splitJsonPairs(json);
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
                String value = keyValue[1].trim();
                result.put(key, parseJsonValue(value));
            }
        }

        return result;
    }

    private static String[] splitJsonPairs(String json) {
        List<String> pairs = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();

        for (char c : json.toCharArray()) {
            if (c == '{' || c == '[') depth++;
            else if (c == '}' || c == ']') depth--;
            else if (c == ',' && depth == 0) {
                pairs.add(current.toString());
                current = new StringBuilder();
                continue;
            }
            current.append(c);
        }

        if (current.length() > 0) {
            pairs.add(current.toString());
        }

        return pairs.toArray(new String[0]);
    }

    private static Object parseJsonValue(String value) {
        value = value.trim();

        if (value.startsWith("\"")) {
            // Строка
            return value.substring(1, value.length() - 1).replace("\\\"", "\"");
        } else if (value.equals("true") || value.equals("false")) {
            // Boolean
            return Boolean.valueOf(value);
        } else if (value.equals("null")) {
            // Null
            return null;
        } else if (value.startsWith("{")) {
            // Объект
            return parseJson(value);
        } else if (value.startsWith("[")) {
            // Массив
            return parseJsonArray(value);
        } else {
            // Число
            try {
                if (value.contains(".")) {
                    return Double.parseDouble(value);
                } else {
                    return Integer.parseInt(value);
                }
            } catch (NumberFormatException e) {
                return value;
            }
        }
    }

    private static List<Object> parseJsonArray(String arrayStr) {
        List<Object> result = new ArrayList<>();
        arrayStr = arrayStr.substring(1, arrayStr.length() - 1).trim();

        if (arrayStr.isEmpty()) {
            return result;
        }

        String[] items = splitJsonArray(arrayStr);
        for (String item : items) {
            result.add(parseJsonValue(item.trim()));
        }

        return result;
    }

    private static String[] splitJsonArray(String arrayStr) {
        List<String> items = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();

        for (char c : arrayStr.toCharArray()) {
            if (c == '{' || c == '[') depth++;
            else if (c == '}' || c == ']') depth--;
            else if (c == ',' && depth == 0) {
                items.add(current.toString());
                current = new StringBuilder();
                continue;
            }
            current.append(c);
        }

        if (current.length() > 0) {
            items.add(current.toString());
        }

        return items.toArray(new String[0]);
    }

    // Удаление сохранения
    public void deleteSave() {
        try {
            Path file = Paths.get(SAVE_DIR, SAVE_FILE);
            if (Files.exists(file)) {
                Files.delete(file);
                System.out.println("Сохранение JSON удалено: " + file.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Ошибка удаления сохранения: " + e.getMessage());
        }
    }

    // Проверка существования сохранения
    public static boolean saveExists() {
        return Files.exists(Paths.get(SAVE_DIR, SAVE_FILE));
    }

    // Остальные методы без изменений...
    public List<Cell> getValidMoves(Checker checker) {
        List<Cell> validMoves = new ArrayList<>();
        if (checker.getColor() != currentPlayer.getColor()) {
            return validMoves;
        }
        boolean mustCapture = mustCaptureExists();
        if (mustCapture) {
            validMoves.addAll(getCaptureMoves(checker));
        } else {
            validMoves.addAll(getNormalMoves(checker));
        }
        return validMoves;
    }

    private boolean mustCaptureExists() {
        for (Checker checker : board.getCheckersByColor(currentPlayer.getColor())) {
            if (!getCaptureMoves(checker).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<Cell> getNormalMoves(Checker checker) {
        List<Cell> moves = new ArrayList<>();
        int x = checker.getCell().getX();
        int y = checker.getCell().getY();

        if (checker.getType() == CheckerType.KING &&
                settings.getVariant() == GameSettings.Variant.RUSSIAN) {
            int[][] dirs = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            for (int[] dir : dirs) {
                int step = 1;
                while (true) {
                    int nx = x + dir[0] * step;
                    int ny = y + dir[1] * step;
                    Cell cell = board.getCell(nx, ny);
                    if (cell == null || !cell.isEmpty()) break;
                    moves.add(cell);
                    step++;
                }
            }
        } else {
            int[][] directions = getDirections(checker);
            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                Cell cell = board.getCell(nx, ny);
                if (cell != null && cell.isEmpty()) {
                    moves.add(cell);
                }
            }
        }
        return moves;
    }

    private void findCaptureMoves(Checker checker, Cell currentCell, List<Cell> captureMoves, List<Checker> capturedCheckers) {
        int x = currentCell.getX();
        int y = currentCell.getY();

        if (checker.getType() == CheckerType.KING && rules.canKingMoveMultipleSteps()) {
            int[][] dirs = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            for (int[] dir : dirs) {
                int step = 1;
                while (true) {
                    int ex = x + dir[0] * step;
                    int ey = y + dir[1] * step;
                    Cell enemyCell = board.getCell(ex, ey);
                    if (enemyCell == null) break;
                    if (enemyCell.hasChecker()) {
                        if (enemyCell.getChecker().getColor() == checker.getColor() ||
                                capturedCheckers.contains(enemyCell.getChecker())) {
                            break;
                        }
                        int jump = 1;
                        while (true) {
                            int tx = x + dir[0] * (step + jump);
                            int ty = y + dir[1] * (step + jump);
                            Cell targetCell = board.getCell(tx, ty);
                            if (targetCell == null) break;
                            if (targetCell.isEmpty()) {
                                captureMoves.add(targetCell);
                                List<Checker> newCaptured = new ArrayList<>(capturedCheckers);
                                newCaptured.add(enemyCell.getChecker());
                                findCaptureMoves(checker, targetCell, captureMoves, newCaptured);
                                jump++;
                            } else {
                                break;
                            }
                        }
                        break;
                    }
                    step++;
                }
            }
        } else {
            int[][] dirs = (checker.getType() == CheckerType.KING)
                    ? new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}
                    : rules.getDirectionsForCapture(checker.getColor());

            for (int[] dir : dirs) {
                int ex = x + dir[0];
                int ey = y + dir[1];
                Cell enemyCell = board.getCell(ex, ey);
                int tx = x + 2 * dir[0];
                int ty = y + 2 * dir[1];
                Cell targetCell = board.getCell(tx, ty);

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
    }

    private List<Cell> getCaptureMoves(Checker checker) {
        List<Cell> captureMoves = new ArrayList<>();
        findCaptureMoves(checker, checker.getCell(), captureMoves, new ArrayList<>());
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
        Cell fromCell = checker.getCell();
        boolean isCapture = isCaptureMove(fromCell, targetCell);

        moveHistory.add(new MoveRecord(
                currentPlayer.getColor(),
                fromCell.getX(), fromCell.getY(),
                targetCell.getX(), targetCell.getY(),
                isCapture
        ));

        board.moveChecker(checker, targetCell);

        if (isCapture) {
            List<Checker> captured = findCapturedCheckers(checker, fromCell, targetCell);
            for (Checker c : captured) {
                board.removeChecker(c);
            }
        }

        checkPromotion(checker);

        if (isCapture && canContinueCapture(checker)) {
            return;
        }

        switchPlayer();
        checkGameOver();
    }

    private boolean isCaptureMove(Cell from, Cell to) {
        return Math.abs(from.getX() - to.getX()) == 2 &&
                Math.abs(from.getY() - to.getY()) == 2;
    }

    private void checkPromotion(Checker checker) {
        int y = checker.getCell().getY();
        if (checker.getType() == CheckerType.REGULAR) {
            if ((checker.isWhite() && y == 0) || (checker.isBlack() && y == 7)) {
                board.promoteToKing(checker);
            }
        }
    }

    private boolean canContinueCapture(Checker checker) {
        return !getCaptureMoves(checker).isEmpty();
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == players[0]) ? players[1] : players[0];
    }

    private List<Checker> findCapturedCheckers(Checker checker, Cell from, Cell to) {
        List<Checker> captured = new ArrayList<>();
        int dx = Integer.signum(to.getX() - from.getX());
        int dy = Integer.signum(to.getY() - from.getY());
        int steps = Math.max(Math.abs(to.getX() - from.getX()), Math.abs(to.getY() - from.getY()));

        if (checker.getType() == CheckerType.KING && rules.canKingMoveMultipleSteps()) {
            for (int i = 1; i < steps; i++) {
                Cell cell = board.getCell(from.getX() + dx * i, from.getY() + dy * i);
                if (cell != null && cell.hasChecker() && cell.getChecker().getColor() != checker.getColor()) {
                    captured.add(cell.getChecker());
                    break;
                }
            }
        } else {
            Cell mid = board.getCell((from.getX() + to.getX()) / 2, (from.getY() + to.getY()) / 2);
            if (mid != null && mid.hasChecker() && mid.getChecker().getColor() != checker.getColor()) {
                captured.add(mid.getChecker());
            }
        }
        return captured;
    }

    private void checkGameOver() {
        List<Checker> currentPlayerCheckers = board.getCheckersByColor(currentPlayer.getColor());
        if (currentPlayerCheckers.isEmpty()) {
            gameState = currentPlayer.getColor() == Color.WHITE ? GameState.BLACK_WIN : GameState.WHITE_WIN;
            System.out.println("Игра окончена! Победитель: " +
                    (gameState == GameState.WHITE_WIN ? "Белые" : "Черные"));
            return;
        }

        // Проверяем, может ли текущий игрок сделать ход
        if (!canPlayerMove(currentPlayer)) {
            gameState = currentPlayer.getColor() == Color.WHITE ? GameState.BLACK_WIN : GameState.WHITE_WIN;
            System.out.println("Игра окончена! Игрок не может сделать ход. Победитель: " +
                    (gameState == GameState.WHITE_WIN ? "Белые" : "Черные"));
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

    // Сохранение истории
    public void saveHistoryToFile() {
        try {
            Path dir = Paths.get("history");
            Files.createDirectories(dir);
            String filename = "game_" + System.currentTimeMillis() + ".txt";
            Path file = dir.resolve(filename);

            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
                writer.println("История игры");
                String mode = (settings.getVariant() == GameSettings.Variant.RUSSIAN) ? "Русские шашки" : "Испанские шашки";
                writer.println("Режим: " + mode);
                writer.println("=".repeat(40));
                for (int i = 0; i < moveHistory.size(); i++) {
                    writer.printf("%2d. %s%n", i + 1, moveHistory.get(i));
                }
            }
            JOptionPane.showMessageDialog(null, "История сохранена в:\n" + file.toAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка сохранения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}