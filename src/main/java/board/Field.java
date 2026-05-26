package board;

import actions.CellActionResult;
import cells.Cell;
import hazards.Mine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Поле создает сетку ячеек, связывает соседей и отслеживает безопасные открытия.
public class Field {
    private final int width;
    private final int height;
    private final int totalMines;
    private Cell[][] grid;
    private final Set<Cell> openedSafeCells = new HashSet<>();

    public Field(int width, int height, int totalMines) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Field dimensions must be positive.");
        }

        if (totalMines < 0 || totalMines >= width * height) {
            throw new IllegalArgumentException("Mine count must be between 0 and number of cells - 1.");
        }

        this.width = width;
        this.height = height;
        this.totalMines = totalMines;
    }

    public void initialize() {
        openedSafeCells.clear();
        grid = new Cell[height][width];

        // Сначала создаем все ячейки, чтобы потом спокойно связать их между собой.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Cell(x, y);
            }
        }

        // На втором проходе назначаем соседей по всем восьми направлениям.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell currentCell = grid[y][x];

                for (int deltaY = -1; deltaY <= 1; deltaY++) {
                    for (int deltaX = -1; deltaX <= 1; deltaX++) {
                        if (deltaX == 0 && deltaY == 0) {
                            continue;
                        }

                        int neighborX = x + deltaX;
                        int neighborY = y + deltaY;

                        if (isWithinBounds(neighborX, neighborY)) {
                            currentCell.addNeighbor(grid[neighborY][neighborX]);
                        }
                    }
                }
            }
        }
    }

    public Cell getCell(int x, int y) {
        ensureInitialized();
        validateCoordinates(x, y);
        return grid[y][x];
    }

    public Cell getCell(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates must not be null.");
        }

        return getCell(coordinates.x(), coordinates.y());
    }

    public List<Cell> getNeighbors(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Cell must not be null.");
        }

        return cell.getNeighbors();
    }

    public void updateNeighborCounts() {
        ensureInitialized();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = grid[y][x];
                int minesAround = 0;

                for (Cell neighbor : cell.getNeighbors()) {
                    if (neighbor.hasMine()) {
                        minesAround++;
                    }
                }

                cell.setAdjacentMinesCount(minesAround);
            }
        }
    }

    public void registerSafeOpen(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Cell must not be null.");
        }

        // Для победы считаем только реально открытые и безопасные ячейки.
        if (!cell.hasMine() && cell.isOpened()) {
            openedSafeCells.add(cell);
        }
    }

    public void unregisterSafeOpen(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Cell must not be null.");
        }

        openedSafeCells.remove(cell);
    }

    public void moveMine(Cell from, Cell to) {
        ensureInitialized();
        if (from == null || to == null) {
            throw new IllegalArgumentException("Cells must not be null.");
        }

        if (from == to) {
            throw new IllegalArgumentException("Mine source and target must be different cells.");
        }

        if (!from.hasMine()) {
            throw new IllegalStateException("Source cell does not contain a mine.");
        }

        if (from.hasDetonatedMine()) {
            throw new IllegalStateException("Detonated mine cannot be relocated.");
        }

        if (!to.canReceiveRelocatedMine()) {
            throw new IllegalStateException("Target cell cannot receive a relocated mine.");
        }

        boolean targetWasOpened = to.isOpened();
        Mine mine = from.removeMine();
        to.placeMine(mine);

        if (targetWasOpened) {
            unregisterSafeOpen(to);
            to.close();
        }

        updateNeighborCounts();
    }

    public void triggerCascadeReveal(Cell startCell) {
        if (startCell == null) {
            throw new IllegalArgumentException("Start cell must not be null.");
        }

        if (startCell.hasMine() || startCell.getAdjacentMinesCount() != 0) {
            return;
        }

        registerSafeOpen(startCell);

        // Используем очередь, чтобы раскрывать пустую область без рекурсии.
        Deque<Cell> queue = new ArrayDeque<>();
        Set<Cell> visited = new HashSet<>();
        queue.add(startCell);
        visited.add(startCell);

        while (!queue.isEmpty()) {
            Cell currentCell = queue.removeFirst();

            for (Cell neighbor : currentCell.getNeighbors()) {
                // Флаги, уже открытые клетки и мины в цепное раскрытие не попадают.
                if (neighbor.isFlagged() || neighbor.isOpened() || neighbor.hasMine()) {
                    continue;
                }

                CellActionResult result = neighbor.open();
                if (result.opened()) {
                    registerSafeOpen(neighbor);
                }

                if (result.cascadeReveal() && visited.add(neighbor)) {
                    queue.addLast(neighbor);
                }
            }
        }
    }

    public boolean isWinConditionMet() {
        return openedSafeCells.size() == getSafeCellsCount();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTotalMines() {
        return totalMines;
    }

    public int getOpenedSafeCellsCount() {
        return openedSafeCells.size();
    }

    public int getFlaggedCellsCount() {
        ensureInitialized();
        int flaggedCells = 0;

        for (Cell cell : getAllCells()) {
            if (cell.isFlagged()) {
                flaggedCells++;
            }
        }

        return flaggedCells;
    }

    public int getSafeCellsCount() {
        return width * height - totalMines;
    }

    public List<Cell> getOpenedCells() {
        ensureInitialized();
        List<Cell> openedCells = new ArrayList<>();

        for (Cell cell : getAllCells()) {
            if (cell.isOpened()) {
                openedCells.add(cell);
            }
        }

        return openedCells;
    }

    public List<Cell> getClosedCells() {
        ensureInitialized();
        List<Cell> closedCells = new ArrayList<>();

        for (Cell cell : getAllCells()) {
            if (!cell.isOpened()) {
                closedCells.add(cell);
            }
        }

        return closedCells;
    }

    public List<Cell> getOpenedBoundaryCells() {
        ensureInitialized();
        List<Cell> boundaryCells = new ArrayList<>();

        for (Cell cell : getOpenedCells()) {
            for (Cell neighbor : cell.getNeighbors()) {
                if (!neighbor.isOpened()) {
                    boundaryCells.add(cell);
                    break;
                }
            }
        }

        return boundaryCells;
    }

    public List<Cell> getAllCells() {
        ensureInitialized();
        List<Cell> cells = new ArrayList<>(width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells.add(grid[y][x]);
            }
        }

        return cells;
    }

    private void ensureInitialized() {
        if (grid == null) {
            throw new IllegalStateException("Field has not been initialized yet.");
        }
    }

    private void validateCoordinates(int x, int y) {
        if (!isWithinBounds(x, y)) {
            throw new IllegalArgumentException("Coordinates are outside of the field.");
        }
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
