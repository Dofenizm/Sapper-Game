package view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

/**
 * Панель статистики текущей партии.
 * Показывает оставшиеся жизни игрока и общее количество мин на поле.
 */
public class StatsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel livesLabel = new JLabel();
    private final JLabel minesLabel = new JLabel();

    /**
     * Создает подписи статистики и задает им начальные значения.
     */
    public StatsPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));
        setOpaque(false);
        styleLabel(livesLabel);
        styleLabel(minesLabel);
        add(livesLabel);
        add(minesLabel);
        setLives(0);
        setMines(0);
    }

    /**
     * Обновляет количество оставшихся жизней.
     */
    public void setLives(int lives) {
        livesLabel.setText("Жизни: " + lives);
    }

    /**
     * Обновляет количество мин на поле.
     */
    public void setMines(int mines) {
        minesLabel.setText("Мины: " + mines);
    }

    /**
     * Применяет единый стиль к текстовым меткам статистики.
     */
    private void styleLabel(JLabel label) {
        label.setForeground(new Color(248, 250, 252));
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
    }
}
