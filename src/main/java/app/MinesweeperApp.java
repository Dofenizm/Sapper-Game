package app;

import view.GameWindow;

import javax.swing.SwingUtilities;

public final class MinesweeperApp {
    private MinesweeperApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}
