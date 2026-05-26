package view;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

/**
 * Панель кнопок управления игрой.
 * Содержит действия для перезапуска партии и выхода из приложения.
 */
public class MenuPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JButton restartButton = new JButton("Новая игра");
    private final JButton exitButton = new JButton("Выход");
    private final JCheckBox sabotageModeCheckBox = new JCheckBox("Диверсант");

    /**
     * Создает кнопки управления и применяет к ним общий стиль.
     */
    public MenuPanel() {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        setOpaque(false);
        styleCheckBox(sabotageModeCheckBox);
        styleButton(restartButton, new Color(34, 197, 94), new Color(20, 83, 45));
        styleButton(exitButton, new Color(239, 68, 68), new Color(127, 29, 29));
        add(sabotageModeCheckBox);
        add(restartButton);
        add(exitButton);
    }

    /**
     * Добавляет обработчик нажатия на кнопку новой игры.
     */
    public void addRestartListener(ActionListener listener) {
        restartButton.addActionListener(listener);
    }

    /**
     * Добавляет обработчик нажатия на кнопку выхода.
     */
    public void addExitListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }

    /**
     * Возвращает выбранный режим диверсанта для новой партии.
     */
    public boolean isSabotageModeEnabled() {
        return sabotageModeCheckBox.isSelected();
    }

    /**
     * Восстанавливает выбранный режим при пересоздании верхней панели.
     */
    public void setSabotageModeEnabled(boolean enabled) {
        sabotageModeCheckBox.setSelected(enabled);
    }

    /**
     * Настраивает внешний вид кнопки.
     */
    private void styleButton(JButton button, Color background, Color border) {
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Настраивает внешний вид переключателя режима диверсанта.
     */
    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        checkBox.setForeground(new Color(226, 232, 240));
        checkBox.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
