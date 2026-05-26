package view;

import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

/**
 * Верхняя панель игры.
 * Объединяет статистику, статус партии и кнопки управления.
 */
public class HeaderPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final StatsPanel statsPanel = new StatsPanel();
    private final MenuPanel menuPanel = new MenuPanel();
    private final JLabel statusLabel = new JLabel("Идёт игра", SwingConstants.CENTER);

    /**
     * Собирает верхнюю панель из трех частей: статистика, статус, меню.
     */
    public HeaderPanel() {
        setLayout(new BorderLayout(12, 4));
        setBackground(new Color(30, 41, 59));
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        statusLabel.setForeground(new Color(226, 232, 240));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(statsPanel, BorderLayout.WEST);
        add(statusLabel, BorderLayout.CENTER);
        add(menuPanel, BorderLayout.EAST);
    }

    /**
     * Обновляет значения жизней и мин на панели статистики.
     */
    public void updateStats(int lives, int mines) {
        statsPanel.setLives(lives);
        statsPanel.setMines(mines);
    }

    /**
     * Показывает текущий статус игры.
     */
    public void showStatus(String text) {
        statusLabel.setText(text);
    }

    /**
     * Передает обработчик кнопки "Новая игра" во вложенную панель меню.
     */
    public void bindRestartAction(ActionListener listener) {
        menuPanel.addRestartListener(listener);
    }

    /**
     * Передает обработчик кнопки "Выход" во вложенную панель меню.
     */
    public void addExitListeners(ActionListener listener) {
        menuPanel.addExitListener(listener);
    }

    /**
     * Возвращает выбранный режим диверсанта для следующей партии.
     */
    public boolean isSabotageModeEnabled() {
        return menuPanel.isSabotageModeEnabled();
    }

    /**
     * Восстанавливает выбранный режим диверсанта после пересоздания панели.
     */
    public void setSabotageModeEnabled(boolean enabled) {
        menuPanel.setSabotageModeEnabled(enabled);
    }
}
