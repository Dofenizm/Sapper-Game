package view;

import board.Field;
import cells.Cell;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

/**
 * Панель игрового поля.
 * Отвечает за создание сетки визуальных клеток и синхронизацию их состояния с моделью Field.
 */
public class BoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private CellView[][] cellGrid;
    private final int fieldWidth;
    private final int fieldHeight;
    private CellInteractionListener interactionListener;

    /**
     * Создает панель поля заданного размера и сразу отрисовывает пустую сетку клеток.
     */
    public BoardPanel(int width, int height) {
        fieldWidth = width;
        fieldHeight = height;
        setBackground(new Color(15, 23, 42));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        renderEmptyGrid();
    }

    /**
     * Пересоздает визуальную сетку клеток.
     * Используется при первом запуске и при новой игре.
     */
    public void renderEmptyGrid() {
        removeAll();
        setLayout(new GridLayout(fieldHeight, fieldWidth, 3, 3));
        cellGrid = new CellView[fieldHeight][fieldWidth];

        for (int y = 0; y < fieldHeight; y++) {
            for (int x = 0; x < fieldWidth; x++) {
                CellView cellView = new CellView(x, y);
                cellView.setCellInteractionListener(interactionListener);
                cellGrid[y][x] = cellView;
                add(cellView);
            }
        }

        revalidate();
        repaint();
    }

    /**
     * Обновляет клетки по текущему состоянию модели без принудительного показа всех мин.
     */
    public void refreshFromField(Field field) {
        refreshFromField(field, false);
    }

    /**
     * Обновляет все визуальные клетки по данным Field.
     * Флаг revealMines нужен в конце игры, чтобы показать расположение мин.
     */
    public void refreshFromField(Field field, boolean revealMines) {
        for (Cell cell : field.getAllCells()) {
            refreshCellVisual(cell, revealMines);
        }
    }

    /**
     * Обновляет одну клетку по простым параметрам.
     * Метод оставлен для соответствия UML-диаграмме и возможного ручного обновления.
     */
    public void refreshCellVisual(int x, int y, String state, int neighborMines, boolean isFlagged) {
        CellView cellView = cellGrid[y][x];
        if (isFlagged) {
            cellView.showFlagged();
        } else if ("OPENED".equalsIgnoreCase(state)) {
            cellView.showOpened(neighborMines);
        } else {
            cellView.showClosed();
        }
    }

    /**
     * Назначает обработчик игровых действий для всех клеток поля.
     */
    public void setCellInteractionListener(CellInteractionListener listener) {
        interactionListener = listener;
        if (cellGrid == null) {
            return;
        }

        for (CellView[] row : cellGrid) {
            for (CellView cellView : row) {
                cellView.setCellInteractionListener(listener);
            }
        }
    }

    /**
     * Назначает обычный Swing ActionListener для всех кнопок-клеток.
     * Используется как дополнительный вариант подключения обработчиков из UML.
     */
    public void setCellInteractionListener(ActionListener listener) {
        if (cellGrid == null) {
            return;
        }

        for (CellView[] row : cellGrid) {
            for (CellView cellView : row) {
                cellView.addActionListener(listener);
            }
        }
    }

    /**
     * Сбрасывает визуальное состояние всех клеток в закрытое.
     */
    public void clearAllCells() {
        if (cellGrid == null) {
            return;
        }

        for (CellView[] row : cellGrid) {
            for (CellView cellView : row) {
                cellView.showClosed();
            }
        }
    }

    /**
     * Включает или отключает игровое поле.
     * Открытые клетки остаются недоступными, а закрытые и флажки можно заблокировать в конце игры.
     */
    public void setBoardEnabled(boolean enabled) {
        if (cellGrid == null) {
            return;
        }

        for (CellView[] row : cellGrid) {
            for (CellView cellView : row) {
                if (!enabled || cellView.getText().isEmpty() || CellView.FLAG_TEXT.equals(cellView.getText())) {
                    cellView.setEnabled(enabled);
                }
            }
        }
    }

    /**
     * Переводит состояние одной модели Cell в соответствующее визуальное состояние CellView.
     */
    private void refreshCellVisual(Cell cell, boolean revealMines) {
        int x = cell.getCoordinates().x();
        int y = cell.getCoordinates().y();
        CellView cellView = cellGrid[y][x];

        if (cell.hasMine() && (cell.hasDetonatedMine() || revealMines)) {
            cellView.showMine(cell.hasDetonatedMine());
            return;
        }

        if (cell.isFlagged()) {
            cellView.showFlagged();
            return;
        }

        if (cell.isOpened()) {
            cellView.showOpened(cell.getAdjacentMinesCount());
            return;
        }

        cellView.showClosed();
    }
}
