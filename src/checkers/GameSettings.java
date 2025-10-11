package checkers;

import java.util.Locale;

public class GameSettings {
    public enum Variant {
        SPANISH,
        RUSSIAN
    }

    private Variant variant = Variant.SPANISH;
    private Locale locale = Locale.getDefault();
    private int cellSize = 60;

    public Variant getVariant() { return variant; }
    public void setVariant(Variant variant) { this.variant = variant; }

    public Locale getLocale() { return locale; }
    public void setLocale(Locale locale) { this.locale = locale; }

    public int getCellSize() { return cellSize; }
    public void setCellSize(int cellSize) { this.cellSize = cellSize; }
}