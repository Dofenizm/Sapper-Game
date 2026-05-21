package view;

import actions.CellAction;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Визуальное представление одной клетки поля.
 * Наследуется от JButton, потому что каждая клетка является нажимаемой кнопкой.
 */
public class CellView extends JButton {
    private static final long serialVersionUID = 1L;

    public static final String FLAG_TEXT = "🚩";
    public static final String MINE_TEXT = "💣";

    private static final Color CLOSED_COLOR = new Color(55, 74, 103);
    private static final Color OPENED_COLOR = new Color(241, 245, 249);
    private static final Color FLAGGED_COLOR = new Color(246, 190, 84);
    private static final Color MINE_COLOR = new Color(220, 67, 67);
    private static final Color BORDER_COLOR = new Color(24, 34, 50);
    private static final Font CELL_FONT = new Font("Segoe UI", Font.BOLD, 17);
    private static final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 20);

    private final int gridX;
    private final int gridY;
    private CellInteractionListener interactionListener;

    /**
     * Создает кнопку-клетку с координатами в сетке игрового поля.
     */
    public CellView(int x, int y) {
        gridX = x;
        gridY = y;
        setPreferredSize(new Dimension(44, 44));
        setMinimumSize(new Dimension(34, 34));
        setMargin(new Insets(0, 0, 0, 0));
        setFocusPainted(false);
        setContentAreaFilled(true);
        setBorderPainted(true);
        setOpaque(true);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(CELL_FONT);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        showClosed();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                handleMouseAction(event);
            }
        });
    }

    /**
     * Возвращает Swing-компонент клетки.
     * Так как CellView уже наследуется от JButton, возвращается текущий объект.
     */
    public JButton getButtonComponent() {
        return this;
    }

    /**
     * Подключает обработчик игровых действий: открытие клетки или установка флажка.
     */
    public void setCellInteractionListener(CellInteractionListener listener) {
        interactionListener = listener;
    }

    /**
     * Обновляет иконку клетки, если внешний код решит использовать Icon вместо текста.
     */
    public void updateIcon(javax.swing.Icon icon) {
        setIcon(icon);
    }

    /**
     * Обновляет текст клетки.
     */
    public void updateText(String text) {
        setText(text);
    }

    /**
     * Показывает закрытую клетку, доступную для клика.
     */
    public void showClosed() {
        setText("");
        setIcon(null);
        setEnabled(true);
        setFont(CELL_FONT);
        setBackground(CLOSED_COLOR);
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    /**
     * Показывает открытую безопасную клетку.
     * Если рядом есть мины, выводит их количество.
     */
    public void showOpened(int adjacentMines) {
        setEnabled(false);
        setIcon(null);
        setFont(CELL_FONT);
        setBackground(OPENED_COLOR);
        setText(adjacentMines > 0 ? String.valueOf(adjacentMines) : "");
        Color numberColor = colorForNumber(adjacentMines);
        setForeground(numberColor);
        setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
    }

    /**
     * Показывает флажок на закрытой клетке.
     */
    public void showFlagged() {
        setEnabled(true);
        setIcon(null);
        setFont(EMOJI_FONT);
        setText(FLAG_TEXT);
        setBackground(FLAGGED_COLOR);
        setForeground(new Color(95, 55, 0));
        setBorder(BorderFactory.createLineBorder(new Color(145, 96, 10)));
    }

    /**
     * Показывает мину.
     * Взорванная мина подсвечивается красным, остальные открываются нейтральным цветом.
     */
    public void showMine(boolean detonated) {
        setEnabled(false);
        setIcon(null);
        setFont(EMOJI_FONT);
        setText(MINE_TEXT);
        setBackground(detonated ? MINE_COLOR : OPENED_COLOR);
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(detonated ? new Color(127, 29, 29) : new Color(203, 213, 225)));
    }

    /**
     * Преобразует клик мыши в игровое действие.
     * Левая кнопка открывает клетку, правая кнопка ставит или снимает флажок.
     */
    private void handleMouseAction(MouseEvent event) {
        if (interactionListener == null || !isEnabled()) {
            return;
        }

        if (SwingUtilities.isRightMouseButton(event)) {
            interactionListener.onCellAction(new board.Coordinates(gridX, gridY), CellAction.TOGGLE_FLAG);
            return;
        }

        if (SwingUtilities.isLeftMouseButton(event)) {
            interactionListener.onCellAction(new board.Coordinates(gridX, gridY), CellAction.OPEN);
        }
    }

    /**
     * Возвращает цвет числа в стиле классического "Сапера".
     */
    private Color colorForNumber(int value) {
        return switch (value) {
            case 1 -> new Color(37, 99, 235);
            case 2 -> new Color(22, 163, 74);
            case 3 -> new Color(220, 38, 38);
            case 4 -> new Color(124, 58, 237);
            case 5 -> new Color(180, 83, 9);
            case 6 -> new Color(8, 145, 178);
            case 7 -> new Color(17, 24, 39);
            case 8 -> new Color(71, 85, 105);
            default -> Color.BLACK;
        };
    }
}
