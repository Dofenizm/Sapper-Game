package actions;

// Результат действия хранит локальный итог, чтобы Game и Field могли продолжить обработку.
public record CellActionResult(
        boolean opened,
        boolean mineTriggered,
        boolean cascadeReveal,
        boolean flagStateChanged
) {

    private static final CellActionResult NO_CHANGE = new CellActionResult(false, false, false, false);
    private static final CellActionResult FLAG_CHANGED = new CellActionResult(false, false, false, true);

    public static CellActionResult noChange() {
        return NO_CHANGE;
    }

    public static CellActionResult flagChanged() {
        return FLAG_CHANGED;
    }

    public static CellActionResult opened(boolean mineTriggered, boolean cascadeReveal) {
        return new CellActionResult(true, mineTriggered, cascadeReveal, false);
    }

    public boolean stateChanged() {
        return opened || flagStateChanged;
    }
}
