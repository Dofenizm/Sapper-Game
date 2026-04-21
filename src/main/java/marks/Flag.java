package marks;

import cells.Cell;

// Флаг помечает подозрительную ячейку и защищает ее от случайного открытия.
public class Flag {
    private final Cell ownerCell;

    public Flag(Cell ownerCell) {
        if (ownerCell == null) {
            throw new IllegalArgumentException("Owner cell must not be null.");
        }

        this.ownerCell = ownerCell;
    }

    public boolean blockOpening() {
        // В этой версии игры флаг всегда запрещает прямое открытие ячейки.
        return true;
    }

    public Cell getOwnerCell() {
        return ownerCell;
    }
}
