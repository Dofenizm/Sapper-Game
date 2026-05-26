package placement;

import board.Field;
import cells.Cell;

public interface MineRelocationStrategy {
    void relocateMines(Field field, Cell openedCell);
}
