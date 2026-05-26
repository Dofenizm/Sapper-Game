package cells;

import actions.CellAction;
import actions.CellActionResult;
import board.Coordinates;
import hazards.Mine;
import marks.Flag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Ячейка хранит свое состояние, соседей и скрытое содержимое.
public class Cell {
    private final Coordinates coordinates;
    private final List<Cell> neighbors = new ArrayList<>();
    private CellState state = CellState.CLOSED;
    private Mine mine;
    private Flag flag;
    private int adjacentMinesCount;

    public Cell(int x, int y) {
        this(new Coordinates(x, y));
    }

    public Cell(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates must not be null.");
        }

        this.coordinates = coordinates;
    }

    public CellActionResult handleAction(CellAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Action must not be null.");
        }

        // Ячейка сама решает, как отреагировать на действие игрока.
        return switch (action) {
            case OPEN -> open();
            case TOGGLE_FLAG -> toggleFlag();
        };
    }

    public CellActionResult open() {
        if (state == CellState.OPENED) {
            return CellActionResult.noChange();
        }

        if (state == CellState.FLAGGED && flag != null && flag.blockOpening()) {
            return CellActionResult.noChange();
        }

        // После успешного открытия ячейка больше не должна считаться закрытой.
        state = CellState.OPENED;

        if (mine != null) {
            mine.detonate();
            return CellActionResult.opened(true, false);
        }

        // Пустая ячейка сообщает, что поле может продолжить цепное открытие.
        return CellActionResult.opened(false, adjacentMinesCount == 0);
    }

    public boolean setFlag() {
        if (state != CellState.CLOSED || flag != null) {
            return false;
        }

        flag = new Flag(this);
        state = CellState.FLAGGED;
        return true;
    }

    public boolean removeFlag() {
        if (state != CellState.FLAGGED || flag == null) {
            return false;
        }

        flag = null;
        state = CellState.CLOSED;
        return true;
    }

    public void placeMine(Mine mine) {
        if (mine == null) {
            throw new IllegalArgumentException("Mine must not be null.");
        }

        if (this.mine != null) {
            throw new IllegalStateException("Cell already contains a mine.");
        }

        this.mine = mine;
        mine.placeIn(this);
    }

    public Mine removeMine() {
        if (mine == null) {
            throw new IllegalStateException("Cell does not contain a mine.");
        }

        Mine removedMine = mine;
        mine = null;
        return removedMine;
    }

    public void close() {
        state = CellState.CLOSED;
        flag = null;
    }

    public boolean canReceiveRelocatedMine() {
        return mine == null && state != CellState.FLAGGED;
    }

    public boolean hasMine() {
        return mine != null;
    }

    public boolean hasDetonatedMine() {
        return mine != null && mine.isDetonated();
    }

    public boolean isFlagged() {
        return state == CellState.FLAGGED;
    }

    public boolean isOpened() {
        return state == CellState.OPENED;
    }

    public CellState getState() {
        return state;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setAdjacentMinesCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Adjacent mine count must be non-negative.");
        }

        adjacentMinesCount = count;
    }

    public int getAdjacentMinesCount() {
        return adjacentMinesCount;
    }

    public void addNeighbor(Cell neighbor) {
        if (neighbor == null) {
            throw new IllegalArgumentException("Neighbor must not be null.");
        }

        if (neighbor == this || neighbors.contains(neighbor)) {
            return;
        }

        neighbors.add(neighbor);
    }

    public List<Cell> getNeighbors() {
        return Collections.unmodifiableList(neighbors);
    }

    private CellActionResult toggleFlag() {
        if (state == CellState.OPENED) {
            return CellActionResult.noChange();
        }

        // Повторное действие на закрытой ячейке либо ставит флаг, либо снимает его.
        return removeFlag() || setFlag()
                ? CellActionResult.flagChanged()
                : CellActionResult.noChange();
    }
}
