package view;

import actions.CellAction;
import board.Coordinates;
import game.Game;
import game.GameState;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Главное окно приложения.
 * Создает игру, размещает панели интерфейса и связывает действия пользователя с моделью Game.
 */
public class GameWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_WIDTH = 9;
    public static final int DEFAULT_HEIGHT = 9;
    public static final int DEFAULT_MINES = 10;
    public static final int DEFAULT_LIVES = 3;

    private Game game;
    private BoardPanel boardPanel;
    private HeaderPanel headerPanel;
    private int fieldWidth;
    private int fieldHeight;
    private int mineCount;
    private int livesCount;

    /**
     * Создает окно с настройками игры по умолчанию.
     */
    public GameWindow() {
        super("Сапёр");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(15, 23, 42));
        if (getContentPane() instanceof JPanel contentPanel) {
            contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        }
        initialize(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINES, DEFAULT_LIVES);
    }

    /**
     * Инициализирует новую игру с шириной, высотой и количеством мин.
     * Количество жизней берется из значения по умолчанию.
     */
    public void initialize(int width, int height, int mines) {
        initialize(width, height, mines, DEFAULT_LIVES);
    }

    /**
     * Полностью создает новую игровую сессию и заново собирает интерфейс окна.
     */
    public void initialize(int width, int height, int mines, int lives) {
        fieldWidth = width;
        fieldHeight = height;
        mineCount = mines;
        livesCount = lives;

        game = new Game();
        game.startGame(fieldWidth, fieldHeight, mineCount, livesCount);

        getContentPane().removeAll();
        headerPanel = new HeaderPanel();
        boardPanel = new BoardPanel(fieldWidth, fieldHeight);
        setupEventListeners();

        add(headerPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        updateView(false);
        pack();
        setMinimumSize(new Dimension(520, 620));
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    /**
     * Подключает обработчики кликов по полю и кнопкам верхней панели.
     */
    public void setupEventListeners() {
        boardPanel.setCellInteractionListener(this::handleCellAction);
        headerPanel.bindRestartAction(event -> restartGame());
        headerPanel.addExitListeners(event -> {
            if (game != null && game.isRunning()) {
                game.endGame();
            }
            dispose();
        });
        registerGlobalListeners();
    }

    /**
     * Место для глобальных обработчиков окна, например горячих клавиш.
     */
    public void registerGlobalListeners() {
        // Reserved for application-level shortcuts from the UML view diagram.
    }

    /**
     * Перезапускает игру с текущими размерами поля, количеством мин и жизней.
     */
    private void restartGame() {
        initialize(fieldWidth, fieldHeight, mineCount, livesCount);
    }

    /**
     * Обрабатывает действие по клетке и передает его в модель Game.
     */
    private void handleCellAction(Coordinates coordinates, CellAction action) {
        if (game == null || !game.isRunning()) {
            return;
        }

        try {
            game.processTurn(coordinates, action);
            updateView(game.getGameState() == GameState.LOST);
            handleFinishedGame();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(this, "Этот ход сейчас нельзя выполнить.", "Ход невозможен", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Синхронизирует все элементы интерфейса с текущим состоянием игры.
     */
    private void updateView(boolean revealMines) {
        boardPanel.refreshFromField(game.getField(), revealMines);
        headerPanel.updateStats(game.getPlayer().getLives(), game.getField().getTotalMines());
        headerPanel.showStatus(statusText(game.getGameState()));
    }

    /**
     * Завершает работу с полем и показывает сообщение, если игра выиграна или проиграна.
     */
    private void handleFinishedGame() {
        if (game.getGameState() == GameState.IN_PROGRESS) {
            return;
        }

        boardPanel.setBoardEnabled(false);
        String message = game.getGameState() == GameState.WON
                ? "Победа! Все безопасные клетки открыты."
                : "Поражение. Жизни закончились.";
        JOptionPane.showMessageDialog(this, message, "Игра окончена", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Преобразует состояние модели в текст для верхней панели.
     */
    private String statusText(GameState state) {
        return switch (state) {
            case IN_PROGRESS -> "Идёт игра";
            case WON -> "Победа";
            case LOST -> "Поражение";
        };
    }
}
