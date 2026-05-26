package placement;

import board.Field;
import cells.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoundaryMineRelocationStrategy implements MineRelocationStrategy {
    private final Random random;

    public BoundaryMineRelocationStrategy() {
        this(new Random());
    }

    public BoundaryMineRelocationStrategy(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random must not be null.");
        }

        this.random = random;
    }

    @Override
    public void relocateMines(Field field, Cell openedCell) {
        if (field == null) {
            throw new IllegalArgumentException("Field must not be null.");
        }

        if (openedCell == null) {
            throw new IllegalArgumentException("Opened cell must not be null.");
        }

        if (!openedCell.isOpened()) {
            return;
        }

        List<Cell> relocatableMines = findRelocatableMines(field);
        List<Cell> boundaryTargets = findBoundaryTargets(field, openedCell);
        if (relocatableMines.isEmpty() || boundaryTargets.isEmpty()) {
            return;
        }

        Cell source = relocatableMines.get(random.nextInt(relocatableMines.size()));
        Cell target = boundaryTargets.get(random.nextInt(boundaryTargets.size()));
        field.moveMine(source, target);
    }

    private List<Cell> findRelocatableMines(Field field) {
        List<Cell> mines = new ArrayList<>();

        for (Cell cell : field.getAllCells()) {
            if (cell.hasMine() && !cell.hasDetonatedMine() && !cell.isOpened() && !cell.isFlagged()) {
                mines.add(cell);
            }
        }

        return mines;
    }

    private List<Cell> findBoundaryTargets(Field field, Cell openedCell) {
        List<Cell> targets = new ArrayList<>();

        for (Cell cell : field.getOpenedCells()) {
            if (cell == openedCell) {
                continue;
            }

            if (isBoundaryCell(cell) && cell.canReceiveRelocatedMine()) {
                targets.add(cell);
            }
        }

        return targets;
    }

    private boolean isBoundaryCell(Cell cell) {
        if (!cell.isOpened()) {
            return false;
        }

        for (Cell neighbor : cell.getNeighbors()) {
            if (!neighbor.isOpened()) {
                return true;
            }
        }

        return false;
    }
}
