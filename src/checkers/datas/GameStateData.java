package checkers.datas;

import checkers.classes.MoveRecord;
import checkers.game.GameSettings;
import checkers.enums.GameState;
import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class GameStateData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<CheckerData> checkers;
    private Color currentPlayerColor;
    private GameState gameState;
    private GameSettings settings;
    private List<MoveRecord> moveHistory;
    private Rectangle windowBounds;
    private Locale locale;

    // Геттеры
    public List<CheckerData> getCheckers() { return checkers; }
    public Color getCurrentPlayerColor() { return currentPlayerColor; }
    public GameState getGameState() { return gameState; }
    public GameSettings getSettings() { return settings; }
    public List<MoveRecord> getMoveHistory() { return moveHistory; }
    public Rectangle getWindowBounds() { return windowBounds; }
    public Locale getLocale() { return locale; }

    // Сеттеры
    public void setCheckers(List<CheckerData> checkers) { this.checkers = checkers; }
    public void setCurrentPlayerColor(Color currentPlayerColor) { this.currentPlayerColor = currentPlayerColor; }
    public void setGameState(GameState gameState) { this.gameState = gameState; }
    public void setSettings(GameSettings settings) { this.settings = settings; }
    public void setMoveHistory(List<MoveRecord> moveHistory) { this.moveHistory = moveHistory; }
    public void setWindowBounds(Rectangle windowBounds) { this.windowBounds = windowBounds; }
    public void setLocale(Locale locale) { this.locale = locale; }
}
