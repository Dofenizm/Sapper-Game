package cells;

import actions.CellAction;
import actions.CellActionResult;
import hazards.Mine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CellTest {

    // Проверяем базовый сценарий: закрытая ячейка должна открыться с первого раза.
    @Test
    void closedCellOpensSuccessfully() {
        Cell cell = new Cell(0, 0);

        CellActionResult result = cell.open();

        assertTrue(result.opened());
        assertEquals(CellState.OPENED, cell.getState());
    }

    // Один и тот же ввод должен сначала поставить флаг, а потом снять его.
    @Test
    void toggleFlagPlacesAndRemovesFlag() {
        Cell cell = new Cell(0, 0);

        CellActionResult placed = cell.handleAction(CellAction.TOGGLE_FLAG);
        CellActionResult removed = cell.handleAction(CellAction.TOGGLE_FLAG);

        assertTrue(placed.flagStateChanged());
        assertTrue(removed.flagStateChanged());
        assertEquals(CellState.CLOSED, cell.getState());
    }

    // Флаг должен защищать ячейку от случайного открытия.
    @Test
    void flaggedCellCannotBeOpened() {
        Cell cell = new Cell(0, 0);
        cell.setFlag();

        CellActionResult result = cell.handleAction(CellAction.OPEN);

        assertFalse(result.opened());
        assertEquals(CellState.FLAGGED, cell.getState());
    }

    // Если в ячейке есть мина, открытие должно зафиксировать подрыв.
    @Test
    void cellWithMineReportsDetonation() {
        Cell cell = new Cell(1, 1);
        Mine mine = new Mine();
        cell.placeMine(mine);

        CellActionResult result = cell.open();

        assertTrue(result.mineTriggered());
        assertTrue(cell.hasDetonatedMine());
        assertSame(cell, mine.getOwnerCell());
    }

    // Ячейка должна хранить подсчитанное число соседних мин без дополнительной логики.
    @Test
    void cellStoresAdjacentMineCount() {
        Cell cell = new Cell(1, 1);

        cell.setAdjacentMinesCount(3);

        assertEquals(3, cell.getAdjacentMinesCount());
    }

    // При перемещении мины ячейка должна отдать объект мины и стать безопасной.
    @Test
    void removeMineReturnsMineAndClearsCell() {
        Cell cell = new Cell(0, 0);
        Mine mine = new Mine();
        cell.placeMine(mine);

        Mine removedMine = cell.removeMine();

        assertSame(mine, removedMine);
        assertFalse(cell.hasMine());
    }

    // Если диверсант кладет мину на открытую область, клетка должна снова стать закрытой.
    @Test
    void closeReturnsOpenedCellToClosedState() {
        Cell cell = new Cell(0, 0);
        cell.open();

        cell.close();

        assertEquals(CellState.CLOSED, cell.getState());
    }

    // Мину можно переложить только в ячейку без мины и без флага.
    @Test
    void canReceiveRelocatedMineRejectsFlaggedOrMinedCells() {
        Cell freeCell = new Cell(0, 0);
        Cell flaggedCell = new Cell(1, 0);
        Cell minedCell = new Cell(2, 0);

        flaggedCell.setFlag();
        minedCell.placeMine(new Mine());

        assertTrue(freeCell.canReceiveRelocatedMine());
        assertFalse(flaggedCell.canReceiveRelocatedMine());
        assertFalse(minedCell.canReceiveRelocatedMine());
    }
}
