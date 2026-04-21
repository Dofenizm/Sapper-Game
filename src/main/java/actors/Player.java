package actors;

// Игрок в этой модели отличается количеством оставшихся жизней.
public class Player {
    private int currentLives;
    private int maxLives;

    public Player(int initialLives) {
        resetLives(initialLives);
    }

    public void resetLives(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Lives count must be positive.");
        }

        maxLives = count;
        currentLives = count;
    }

    public void loseLife() {
        // Жизни не должны уходить в отрицательные значения.
        if (currentLives > 0) {
            currentLives--;
        }
    }

    public int getLives() {
        return currentLives;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public boolean isAlive() {
        return currentLives > 0;
    }
}
