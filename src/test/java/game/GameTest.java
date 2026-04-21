package game;

import board.Coordinates;
import board.Field;
import actions.CellAction;
import hazards.Mine;
import placement.Saboteur;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    // После запуска игра должна перейти в активное состояние.
    @Test
    void startGameSetsInProgressState() {
        Game game = new Game(new Saboteur(new Random(0)));

        game.startGame(3, 3, 1, 2);

        assertEquals(GameState.IN_PROGRESS, game.getGameState());
        assertTrue(game.isRunning());
    }

    // Открытие безопасной клетки не должно отнимать жизнь у игрока.
    @Test
    void safeTurnOpensCellWithoutLosingLife() {
        Field field = createManualField(2, 2, new Coordinates(1, 1));
        Game game = new Game(new StubSaboteur(field));
        game.startGame(2, 2, 1, 2);

        game.processTurn(new Coordinates(0, 0), CellAction.OPEN);

        assertTrue(game.getField().getCell(0, 0).isOpened());
        assertEquals(2, game.getPlayer().getLives());
        assertEquals(GameState.IN_PROGRESS, game.getGameState());
    }

    // Подрыв на мине должен уменьшать запас жизней, но не обязан сразу завершать игру.
    @Test
    void mineExplosionReducesLife() {
        Field field = createManualField(2, 2, new Coordinates(1, 1));
        Game game = new Game(new StubSaboteur(field));
        game.startGame(2, 2, 1, 2);

        game.processTurn(new Coordinates(1, 1), CellAction.OPEN);

        assertEquals(1, game.getPlayer().getLives());
        assertEquals(GameState.IN_PROGRESS, game.getGameState());
        assertTrue(game.getField().getCell(1, 1).hasDetonatedMine());
    }

    // Если потеряна последняя жизнь, игра обязана закончиться поражением.
    @Test
    void lastLostLifeEndsGameWithDefeat() {
        Field field = createManualField(2, 2, new Coordinates(1, 1));
        Game game = new Game(new StubSaboteur(field));
        game.startGame(2, 2, 1, 1);

        game.processTurn(new Coordinates(1, 1), CellAction.OPEN);

        assertEquals(GameState.LOST, game.getGameState());
        assertFalse(game.isRunning());
    }

    // Победа наступает после открытия всех клеток без мин.
    @Test
    void openingAllSafeCellsEndsGameWithVictory() {
        Field field = createManualField(2, 2, new Coordinates(1, 1));
        Game game = new Game(new StubSaboteur(field));
        game.startGame(2, 2, 1, 2);

        game.processTurn(new Coordinates(0, 0), CellAction.OPEN);
        game.processTurn(new Coordinates(1, 0), CellAction.OPEN);
        game.processTurn(new Coordinates(0, 1), CellAction.OPEN);

        assertEquals(GameState.WON, game.getGameState());
        assertFalse(game.isRunning());
    }

    // После завершения партии новые ходы принимать уже нельзя.
    @Test
    void turnsAfterGameEndAreRejected() {
        Field field = createManualField(2, 2, new Coordinates(1, 1));
        Game game = new Game(new StubSaboteur(field));
        game.startGame(2, 2, 1, 1);
        game.processTurn(new Coordinates(1, 1), CellAction.OPEN);

        assertThrows(IllegalStateException.class,
                () -> game.processTurn(new Coordinates(0, 0), CellAction.OPEN));
    }

    // Попытка открыть ячейку с флагом не должна менять состояние поля.
    @Test
    void flaggedCellStaysClosedWhenOpenActionIsUsed() {
        Field field = createManualField(2, 2, new Coordinates(1, 1));
        Game game = new Game(new StubSaboteur(field));
        game.startGame(2, 2, 1, 2);

        game.processTurn(new Coordinates(0, 0), CellAction.TOGGLE_FLAG);
        game.processTurn(new Coordinates(0, 0), CellAction.OPEN);

        assertTrue(game.getField().getCell(0, 0).isFlagged());
        assertEquals(GameState.IN_PROGRESS, game.getGameState());
        assertEquals(0, game.getField().getOpenedSafeCellsCount());
    }

    // Подготавливаем поле вручную, чтобы тесты не зависели от случайной расстановки мин.
    private static Field createManualField(int width, int height, Coordinates mineCoordinates) {
        Field field = new Field(width, height, 1);
        field.initialize();
        field.getCell(mineCoordinates).placeMine(new Mine());
        field.updateNeighborCounts();
        return field;
    }

    private static final class StubSaboteur extends Saboteur {
        private final Field preparedField;

        private StubSaboteur(Field preparedField) {
            super(new Random(0));
            this.preparedField = preparedField;
        }

        @Override
        public Field createAndInitializeField(int width, int height, int mineCount) {
            // В тестах игра всегда получает заранее известное поле.
            return preparedField;
        }
    }
}
