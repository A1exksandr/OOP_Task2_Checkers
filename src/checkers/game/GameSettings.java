package checkers.game;

import java.util.Locale;
import java.io.Serializable;

public class GameSettings implements Serializable {
    private static final long serialVersionUID = 1L;

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