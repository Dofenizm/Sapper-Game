package board;

// Координаты ячейки на прямоугольном поле.
public record Coordinates(int x, int y) {

    public Coordinates {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates must be non-negative.");
        }
    }
}
