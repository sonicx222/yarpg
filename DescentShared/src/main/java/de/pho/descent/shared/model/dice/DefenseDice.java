package de.pho.descent.shared.model.dice;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author pho
 */
public enum DefenseDice {
    BROWN(new int[]{0,0,0,1,1,2}),
    GRAY(new int[]{0,1,1,1,2,3}),
    BLACK(new int[]{0,2,2,2,3,4});
    
    private final int[] values;
    
    private DefenseDice(int[] values) {
        this.values = values;
    }

    public int[] getValues() {
        return values;
    }
    
    public int roll() {
        return values[ThreadLocalRandom.current().nextInt(0, 5 + 1)];
    }
}
