// GameSettings.java
package checkers;

import java.util.Locale;

public class GameSettings {
    public enum Variant {
        SPANISH,
        RUSSIAN
    }

    private Variant variant = Variant.SPANISH;
    private Locale locale = Locale.getDefault();
    private int boardSize = 8;

    // Геттеры и сеттеры
    public Variant getVariant() { return variant; }
    public void setVariant(Variant variant) { this.variant = variant; }

    public Locale getLocale() { return locale; }
    public void setLocale(Locale locale) { this.locale = locale; }

    public int getBoardSize() { return boardSize; }
    public void setBoardSize(int boardSize) { this.boardSize = boardSize; }
}
