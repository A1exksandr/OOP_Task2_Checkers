// GameRules.java тоже должен быть сериализуемым
package checkers.game;

import java.awt.Color;
import java.io.Serializable;

public class GameRules implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum RuleType {
        RUSSIAN,
        SPANISH
    }

    private final RuleType ruleType;

    public GameRules(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public boolean isRussian() {
        return ruleType == RuleType.RUSSIAN;
    }

    public int[][] getDirectionsForNormalChecker(Color color) {
        if (isRussian()) {
            if (color == Color.WHITE) {
                return new int[][]{{1, -1}, {-1, -1}};
            } else {
                return new int[][]{{1, 1}, {-1, 1}};
            }
        } else {
            if (color == Color.WHITE) {
                return new int[][]{{1, -1}, {-1, -1}};
            } else {
                return new int[][]{{1, 1}, {-1, 1}};
            }
        }
    }

    public int[][] getDirectionsForCapture(Color color) {
        if (isRussian()) {
            return new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        } else {
            if (color == Color.WHITE) {
                return new int[][]{{1, -1}, {-1, -1}};
            } else {
                return new int[][]{{1, 1}, {-1, 1}};
            }
        }
    }

    public boolean canKingMoveMultipleSteps() {
        return isRussian();
    }
}