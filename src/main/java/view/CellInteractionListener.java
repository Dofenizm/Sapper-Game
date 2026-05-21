package view;

import actions.CellAction;
import board.Coordinates;

/**
 * Интерфейс обратного вызова для действий пользователя по клетке.
 * Отделяет визуальную кнопку CellView от игровой логики обработки хода.
 */
@FunctionalInterface
public interface CellInteractionListener {
    /**
     * Вызывается, когда пользователь выполняет действие над клеткой поля.
     */
    void onCellAction(Coordinates coordinates, CellAction action);
}
