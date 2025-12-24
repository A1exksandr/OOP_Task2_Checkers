package checkers.datas;

import checkers.enums.CheckerType;
import java.awt.*;
import java.io.Serializable;

public class CheckerData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Color color;
    private CheckerType type;
    private int x;
    private int y;

    public CheckerData(Color color, CheckerType type, int x, int y) {
        this.color = color;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    // Геттеры
    public Color getColor() { return color; }
    public CheckerType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }

    // Сеттеры
    public void setColor(Color color) { this.color = color; }
    public void setType(CheckerType type) { this.type = type; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
