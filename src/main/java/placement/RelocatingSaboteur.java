package placement;

import board.Field;
import cells.Cell;

import java.util.Random;

public class RelocatingSaboteur extends Saboteur {
    private final MineRelocationStrategy relocationStrategy;

    public RelocatingSaboteur() {
        this(new BoundaryMineRelocationStrategy());
    }

    public RelocatingSaboteur(MineRelocationStrategy relocationStrategy) {
        if (relocationStrategy == null) {
            throw new IllegalArgumentException("Relocation strategy must not be null.");
        }

        this.relocationStrategy = relocationStrategy;
    }

    public RelocatingSaboteur(Random random, MineRelocationStrategy relocationStrategy) {
        super(random);
        if (relocationStrategy == null) {
            throw new IllegalArgumentException("Relocation strategy must not be null.");
        }

        this.relocationStrategy = relocationStrategy;
    }

    @Override
    public void afterSafeTurn(Field field, Cell openedCell) {
        relocateAfterTurn(field, openedCell);
    }

    public void relocateAfterTurn(Field field, Cell openedCell) {
        relocationStrategy.relocateMines(field, openedCell);
    }
}
