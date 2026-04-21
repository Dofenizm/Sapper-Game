package placement;

import board.Field;
import cells.Cell;
import hazards.Mine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Расстановщик создает поле и случайно раскладывает по нему мины.
public class Saboteur {
    private final Random random;

    public Saboteur() {
        this(new Random());
    }

    public Saboteur(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random must not be null.");
        }

        this.random = random;
    }

    public Field createAndInitializeField(int width, int height, int mineCount) {
        Field field = new Field(width, height, mineCount);
        field.initialize();
        placeInitialMines(field, mineCount);
        field.updateNeighborCounts();
        return field;
    }

    public void placeInitialMines(Field field, int mineCount) {
        if (field == null) {
            throw new IllegalArgumentException("Field must not be null.");
        }

        List<Cell> availableCells = new ArrayList<>(field.getAllCells());
        if (mineCount < 0 || mineCount > availableCells.size()) {
            throw new IllegalArgumentException("Mine count is out of range.");
        }

        for (int index = 0; index < mineCount; index++) {
            // Удаляем выбранную ячейку из списка, чтобы не поставить две мины в одно место.
            int randomIndex = random.nextInt(availableCells.size());
            Cell targetCell = availableCells.remove(randomIndex);
            targetCell.placeMine(new Mine());
        }
    }
}
