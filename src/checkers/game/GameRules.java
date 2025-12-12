// checkers/game/GameRules.java
package checkers.game;

import checkers.classes.Checker;
import checkers.enums.CheckerType;
import java.awt.Color;

public class GameRules {
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
            // В русских шашках обычные шашки **ходят только вперёд**, но **бьют и вперёд, и назад**
            // Однако для *обычных ходов* (не взятий) — только вперёд
            if (color == Color.WHITE) {
                return new int[][]{{1, -1}, {-1, -1}};
            } else {
                return new int[][]{{1, 1}, {-1, 1}};
            }
        } else {
            // Испанские — аналогично: ходят только вперёд
            if (color == Color.WHITE) {
                return new int[][]{{1, -1}, {-1, -1}};
            } else {
                return new int[][]{{1, 1}, {-1, 1}};
            }
        }
    }

    public int[][] getDirectionsForCapture(Color color) {
        if (isRussian()) {
            // В русских шашках **все фигуры бьют в любых направлениях**
            return new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        } else {
            // В испанских — обычные шашки бьют только вперёд
            if (color == Color.WHITE) {
                return new int[][]{{1, -1}, {-1, -1}};
            } else {
                return new int[][]{{1, 1}, {-1, 1}};
            }
        }
    }

    public boolean canKingMoveMultipleSteps() {
        return isRussian(); // В испанских дамка ходит на 1 клетку, в русских — сколько угодно
    }
}