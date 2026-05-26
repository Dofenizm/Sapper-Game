package placement;

import board.Field;
import cells.Cell;
import hazards.Mine;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundaryMineRelocationStrategyTest {

    // Стратегия переносит невзорванную мину на другую открытую границу и закрывает цель.
    @Test
    void relocateMinesMovesMineToAnotherOpenedBoundaryAndClosesTarget() {
        Field field = new Field(3, 3, 1);
        field.initialize();
        Cell source = field.getCell(2, 2);
        Cell openedCell = field.getCell(0, 0);
        Cell target = field.getCell(1, 0);
        Mine mine = new Mine();
        source.placeMine(mine);
        field.updateNeighborCounts();
        openedCell.open();
        field.registerSafeOpen(openedCell);
        target.open();
        field.registerSafeOpen(target);

        MineRelocationStrategy strategy = new BoundaryMineRelocationStrategy(new Random(0));
        strategy.relocateMines(field, openedCell);

        assertFalse(source.hasMine());
        assertTrue(target.hasMine());
        assertFalse(target.isOpened());
        assertFalse(openedCell.hasMine());
        assertTrue(openedCell.isOpened());
        assertSame(target, mine.getOwnerCell());
        assertEquals(1, field.getOpenedSafeCellsCount());
    }

    // Взорванные мины не должны перемещаться, потому что они уже отработали как опасность.
    @Test
    void relocateMinesDoesNotMoveDetonatedMine() {
        Field field = new Field(2, 2, 1);
        field.initialize();
        Cell mineCell = field.getCell(1, 1);
        Cell openedCell = field.getCell(0, 0);
        mineCell.placeMine(new Mine());
        mineCell.open();
        openedCell.open();
        field.registerSafeOpen(openedCell);

        MineRelocationStrategy strategy = new BoundaryMineRelocationStrategy(new Random(0));
        strategy.relocateMines(field, openedCell);

        assertTrue(mineCell.hasMine());
        assertTrue(mineCell.hasDetonatedMine());
        assertFalse(openedCell.hasMine());
        assertTrue(openedCell.isOpened());
    }
}
