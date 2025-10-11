package checkers;

public class GameRules {
    public enum RuleType {
        RUSSIAN,    // Русские шашки
        SPANISH     // Испанские шашки (текущая реализация)
    }

    private RuleType ruleType;

    public GameRules(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public boolean canKingMoveMultipleCells() {
        return ruleType == RuleType.RUSSIAN;
    }

    public boolean canRegularCaptureBackward() {
        return ruleType == RuleType.RUSSIAN;
    }

    public boolean isKingMoveSingleStep() {
        return ruleType == RuleType.SPANISH;
    }
}
