package placement;

import board.Coordinates;
import board.Field;
import cells.Cell;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaboteurTest {

    // Расстановщик должен создать поле нужного размера и положить ровно заданное число мин.
    @Test
    void saboteurCreatesFieldWithExpectedSizeAndMineCount() {
        Saboteur saboteur = new Saboteur(new Random(7));

        Field field = saboteur.createAndInitializeField(4, 3, 4);

        assertEquals(4, field.getWidth());
        assertEquals(3, field.getHeight());
        assertEquals(4, countMines(field));
    }

    // Одинаковое зерно генератора должно давать одинаковую расстановку мин.
    @Test
    void saboteurPlacementIsDeterministicForSameSeed() {
        Saboteur firstSaboteur = new Saboteur(new Random(42));
        Saboteur secondSaboteur = new Saboteur(new Random(42));

        Field firstField = firstSaboteur.createAndInitializeField(3, 3, 3);
        Field secondField = secondSaboteur.createAndInitializeField(3, 3, 3);

        assertEquals(mineCoordinates(firstField), mineCoordinates(secondField));
    }

    // При случайной расстановке две мины не должны попадать в одну и ту же клетку.
    @Test
    void saboteurPlacesMinesIntoUniqueCells() {
        Saboteur saboteur = new Saboteur(new Random(1));

        Field field = saboteur.createAndInitializeField(4, 4, 5);
        Set<Coordinates> uniqueMineCoordinates = new HashSet<>(mineCoordinates(field));

        assertEquals(5, uniqueMineCoordinates.size());
    }

    // Вспомогательно считаем все клетки, в которых реально лежат мины.
    private static int countMines(Field field) {
        int count = 0;
        for (Cell cell : field.getAllCells()) {
            if (cell.hasMine()) {
                count++;
            }
        }

        return count;
    }

    // Собираем координаты мин, чтобы удобно сравнивать расстановки между полями.
    private static List<Coordinates> mineCoordinates(Field field) {
        List<Coordinates> coordinates = new ArrayList<>();

        for (Cell cell : field.getAllCells()) {
            if (cell.hasMine()) {
                coordinates.add(cell.getCoordinates());
            }
        }

        return coordinates;
    }
}
