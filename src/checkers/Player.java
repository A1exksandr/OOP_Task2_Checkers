package checkers;

import java.awt.*;

public class Player {
    private final Color color;
    private final PlayerType type;
    private String name;

    public Player(Color color, PlayerType type, String name) {
        this.color = color;
        this.type = type;
        this.name = name;
    }

    // Геттеры
    public Color getColor() { return color; }
    public PlayerType getType() { return type; }
    public String getName() { return name; }

    public boolean isHuman() {
        return type == PlayerType.HUMAN;
    }

    public boolean isComputer() {
        return type == PlayerType.COMPUTER;
    }
}