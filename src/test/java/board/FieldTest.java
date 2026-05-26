package board;

import hazards.Mine;
import cells.Cell;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldTest {

    // После инициализации поле должно создать все клетки по размеру сетки.
    @Test
    void fieldCreatesExpectedAmountOfCells() {
        Field field = new Field(3, 2, 1);
        field.initialize();

        assertEquals(6, field.getAllCells().size());
    }

    // Углы, края и центр должны получать разное число соседей.
    @Test
    void cellsReceiveCorrectNeighborCountsByPosition() {
        Field field = new Field(3, 3, 1);
        field.initialize();

        assertEquals(3, field.getCell(0, 0).getNeighbors().size());
        assertEquals(5, field.getCell(1, 0).getNeighbors().size());
        assertEquals(8, field.getCell(1, 1).getNeighbors().size());
    }

    // После расстановки мин поле должно корректно пересчитать цифры вокруг них.
    @Test
    void neighborMineCountsAreCalculatedCorrectly() {
        Field field = new Field(3, 3, 2);
        field.initialize();
        field.getCell(0, 0).placeMine(new Mine());
        field.getCell(2, 2).placeMine(new Mine());

        field.updateNeighborCounts();

        assertEquals(1, field.getCell(0, 1).getAdjacentMinesCount());
        assertEquals(2, field.getCell(1, 1).getAdjacentMinesCount());
        assertEquals(1, field.getCell(2, 1).getAdjacentMinesCount());
    }

    // Пустая область должна раскрываться каскадом вместе с граничными числовыми ячейками.
    @Test
    void cascadeRevealOpensZeroRegionAndBorderNumbers() {
        Field field = new Field(3, 3, 1);
        field.initialize();
        field.getCell(2, 2).placeMine(new Mine());
        field.updateNeighborCounts();

        Cell start = field.getCell(0, 0);
        start.open();
        field.registerSafeOpen(start);
        field.triggerCascadeReveal(start);

        assertTrue(field.getCell(0, 0).isOpened());
        assertTrue(field.getCell(1, 1).isOpened());
        assertTrue(field.getCell(2, 1).isOpened());
        assertFalse(field.getCell(2, 2).isOpened());
        assertEquals(8, field.getOpenedSafeCellsCount());
    }

    // Победа должна засчитываться только после открытия всех безопасных клеток.
    @Test
    void winConditionRequiresAllSafeCellsToBeOpened() {
        Field field = new Field(2, 2, 1);
        field.initialize();
        field.getCell(1, 1).placeMine(new Mine());
        field.updateNeighborCounts();

        openSafeCell(field, 0, 0);
        openSafeCell(field, 1, 0);

        assertFalse(field.isWinConditionMet());

        openSafeCell(field, 0, 1);

        assertTrue(field.isWinConditionMet());
    }

    // Перемещение мины должно перенести сам объект Mine, закрыть открытую цель и пересчитать цифры.
    @Test
    void moveMineTransfersMineClosesOpenedTargetAndUpdatesNeighborCounts() {
        Field field = new Field(3, 3, 1);
        field.initialize();
        Cell source = field.getCell(2, 2);
        Cell target = field.getCell(1, 1);
        Mine mine = new Mine();
        source.placeMine(mine);
        field.updateNeighborCounts();
        target.open();
        field.registerSafeOpen(target);

        field.moveMine(source, target);

        assertFalse(source.hasMine());
        assertTrue(target.hasMine());
        assertFalse(target.isOpened());
        assertSame(target, mine.getOwnerCell());
        assertEquals(0, field.getOpenedSafeCellsCount());
        assertEquals(1, field.getCell(0, 0).getAdjacentMinesCount());
    }

    // Граница открытой области состоит из открытых клеток, рядом с которыми еще есть закрытые клетки.
    @Test
    void openedBoundaryCellsContainOpenedCellsWithClosedNeighbors() {
        Field field = new Field(3, 3, 1);
        field.initialize();
        openSafeCell(field, 1, 1);

        assertTrue(field.getOpenedBoundaryCells().contains(field.getCell(1, 1)));
    }

    // Поле должно уметь считать все установленные флаги.
    @Test
    void flaggedCellsCountReflectsCurrentFlags() {
        Field field = new Field(3, 3, 2);
        field.initialize();

        field.getCell(0, 0).setFlag();
        field.getCell(1, 1).setFlag();

        assertEquals(2, field.getFlaggedCellsCount());

        field.getCell(0, 0).removeFlag();

        assertEquals(1, field.getFlaggedCellsCount());
    }

    // Вспомогательный метод открывает безопасную ячейку и сразу регистрирует ее для проверки победы.
    private static void openSafeCell(Field field, int x, int y) {
        Cell cell = field.getCell(x, y);
        cell.open();
        field.registerSafeOpen(cell);
    }
}
