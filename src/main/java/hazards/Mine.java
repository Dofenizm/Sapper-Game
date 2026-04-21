package hazards;

import cells.Cell;

// Мина знает, в какой ячейке лежит, и была ли уже взорвана.
public class Mine {
    private Cell ownerCell;
    private boolean detonated;

    public void detonate() {
        detonated = true;
    }

    public void placeIn(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Owner cell must not be null.");
        }

        ownerCell = cell;
    }

    public Cell getOwnerCell() {
        return ownerCell;
    }

    public boolean isDetonated() {
        return detonated;
    }
}
