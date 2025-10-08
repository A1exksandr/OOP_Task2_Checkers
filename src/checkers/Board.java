package checkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private Cell[][] cells;
    private List<Checker> checkers;

    public Board() {
        initializeBoard();
        setupCheckers();
    }

    private void initializeBoard() {
        cells = new Cell[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Color cellColor = ((x + y) % 2 == 0) ? Color.LIGHT_GRAY : Color.DARK_GRAY;
                cells[x][y] = new Cell(x, y, cellColor);
            }
        }
    }

    private void setupCheckers() {
        checkers = new ArrayList<>();

        // Белые шашки (нижняя часть - y = 5,6,7)
        for (int y = 5; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (cells[x][y].getColor() == Color.DARK_GRAY) {
                    checkers.add(new Checker(Color.WHITE, cells[x][y]));
                }
            }
        }

        // Черные шашки (верхняя часть - y = 0,1,2)
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 8; x++) {
                if (cells[x][y].getColor() == Color.DARK_GRAY) {
                    checkers.add(new Checker(Color.BLACK, cells[x][y]));
                }
            }
        }
    }

    // Геттеры
    public Cell getCell(int x, int y) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            return cells[x][y];
        }
        return null;
    }

    public List<Checker> getCheckers() {
        return checkers;
    }

    public List<Checker> getCheckersByColor(Color color) {
        List<Checker> result = new ArrayList<>();
        for (Checker checker : checkers) {
            if (checker.getColor() == color) {
                result.add(checker);
            }
        }
        return result;
    }

    // Перемещение шашки
    public void moveChecker(Checker checker, Cell targetCell) {
        Cell fromCell = checker.getCell();
        fromCell.removeChecker();
        targetCell.setChecker(checker);
        checker.setCell(targetCell);
    }

    // Удаление шашки
    public void removeChecker(Checker checker) {
        checker.getCell().removeChecker();
        checkers.remove(checker);
    }

    // Превращение в дамку
    public void promoteToKing(Checker checker) {
        checker.promoteToKing();
    }
}
