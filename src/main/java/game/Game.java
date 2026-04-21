package game;

import board.Coordinates;
import board.Field;
import actions.CellAction;
import actions.CellActionResult;
import actors.Player;
import cells.Cell;
import placement.Saboteur;

// Игра связывает игрока, поле и расстановщика мин в один игровой цикл.
public class Game {
    private final Saboteur saboteur;
    private Field field;
    private Player player;
    private GameState gameState;
    private boolean running;

    public Game() {
        this(new Saboteur());
    }

    public Game(Saboteur saboteur) {
        if (saboteur == null) {
            throw new IllegalArgumentException("Saboteur must not be null.");
        }

        this.saboteur = saboteur;
    }

    public void startGame(int width, int height, int mineCount, int initialLives) {
        player = new Player(initialLives);
        field = saboteur.createAndInitializeField(width, height, mineCount);
        gameState = GameState.IN_PROGRESS;
        running = true;
        checkGameState();
    }

    public void processTurn(Coordinates coordinates, CellAction action) {
        ensureGameIsRunning();

        Cell cell = field.getCell(coordinates);
        CellActionResult result = cell.handleAction(action);

        // Если ячейка безопасна, игра учитывает ее для победы и при необходимости запускает каскад.
        if (result.opened() && !result.mineTriggered()) {
            field.registerSafeOpen(cell);
            if (result.cascadeReveal()) {
                field.triggerCascadeReveal(cell);
            }
        }

        // Урон игроку наносится только после подтвержденного подрыва мины.
        if (result.mineTriggered()) {
            player.loseLife();
        }

        checkGameState();
    }

    public void checkGameState() {
        if (player == null || field == null) {
            return;
        }

        // Поражение имеет приоритет: если жизней нет, дальнейшие проверки не нужны.
        if (!player.isAlive()) {
            gameState = GameState.LOST;
            running = false;
            return;
        }

        // Победа наступает, когда открыты все безопасные клетки поля.
        if (field.isWinConditionMet()) {
            gameState = GameState.WON;
            running = false;
            return;
        }

        gameState = GameState.IN_PROGRESS;
        running = true;
    }

    public void endGame() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public Field getField() {
        return field;
    }

    public Player getPlayer() {
        return player;
    }

    public GameState getGameState() {
        return gameState;
    }

    private void ensureGameIsRunning() {
        if (!running || gameState != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress.");
        }
    }
}
