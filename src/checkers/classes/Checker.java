package checkers.classes;

import checkers.enums.CheckerType;

import java.awt.*;

public class Checker {
    private final Color color;
    private CheckerType type;
    private Cell cell;

    public Checker(Color color, Cell cell) {
        this.color = color;
        this.type = CheckerType.REGULAR;
        this.cell = cell;
        cell.setChecker(this);
    }

    // Геттеры
    public Color getColor() { return color; }
    public CheckerType getType() { return type; }
    public Cell getCell() { return cell; }

    // Сеттеры
    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public void promoteToKing() {
        this.type = CheckerType.KING;
    }

    public boolean isWhite() {
        return color == Color.WHITE;
    }

    public boolean isBlack() {
        return color == Color.BLACK;
    }
}