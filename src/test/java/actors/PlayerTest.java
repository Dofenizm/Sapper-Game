package actors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerTest {

    // Сброс жизней должен одинаково обновлять текущее и максимальное значение.
    @Test
    void resetLivesSetsCurrentAndMaximumLives() {
        Player player = new Player(2);

        player.resetLives(4);

        assertEquals(4, player.getLives());
        assertEquals(4, player.getMaxLives());
    }

    // Обычная потеря жизни должна уменьшать счетчик, пока игрок еще жив.
    @Test
    void loseLifeDecreasesLivesUntilZero() {
        Player player = new Player(2);

        player.loseLife();

        assertEquals(1, player.getLives());
        assertTrue(player.isAlive());
    }

    // Даже при повторных вызовах количество жизней не должно уходить ниже нуля.
    @Test
    void livesDoNotGoBelowZero() {
        Player player = new Player(1);

        player.loseLife();
        player.loseLife();

        assertEquals(0, player.getLives());
        assertFalse(player.isAlive());
    }
}
