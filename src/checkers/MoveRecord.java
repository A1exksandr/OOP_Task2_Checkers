package checkers;

import java.awt.*;

public class MoveRecord {
    private final Color playerColor;
    private final int fromX, fromY, toX, toY;
    private final boolean isCapture;

    public MoveRecord(Color playerColor, int fromX, int fromY, int toX, int toY, boolean isCapture) {
        this.playerColor = playerColor;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.isCapture = isCapture;
    }

    @Override
    public String toString() {
        String player = (playerColor == Color.WHITE) ? "Белые" : "Чёрные";
        String capture = isCapture ? " (взятие)" : "";
        return String.format("%s: %c%d → %c%d%s",
                player,
                'a' + fromX, 8 - fromY,
                'a' + toX, 8 - toY,
                capture);
    }
}