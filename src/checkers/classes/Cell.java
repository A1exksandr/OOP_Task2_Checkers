package checkers.classes;

import java.awt.*;

public class Cell {
    private final int x;
    private final int y;
    private final Color color;
    private Checker checker;

    public Cell(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.checker = null;
    }

    // Геттеры
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
    public Checker getChecker() { return checker; }

    // Проверки
    public boolean hasChecker() { return checker != null; }
    public boolean isEmpty() { return checker == null; }

    // Установка/удаление шашки
    public void setChecker(Checker checker) { this.checker = checker; }
    public void removeChecker() { this.checker = null; }

    @Override
    public String toString() {
        return "Cell{" + x + "," + y + "}";
    }
}