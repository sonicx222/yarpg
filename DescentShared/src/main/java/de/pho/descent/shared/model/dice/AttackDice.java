package de.pho.descent.shared.model.dice;

import static de.pho.descent.shared.model.dice.AttackDiceResult.*;

/**
 *
 * @author pho
 */
public enum AttackDice {
    BLUE(new AttackDiceResult[]{FIZZLE,BLUE2,BLUE3,BLUE4,BLUE5,BLUE6}),
    RED(new AttackDiceResult[]{RED1,RED2,RED2,RED2,RED3,RED4}),
    YELLOW(new AttackDiceResult[]{YELLOW1,YELLOW2,YELLOW3,YELLOW4,YELLOW5,YELLOW6}),
    GREEN(new AttackDiceResult[]{GREEN1,GREEN2,GREEN3,GREEN4,GREEN5,GREEN6});
    
    private final AttackDiceResult[] values;
    
        private AttackDice(AttackDiceResult[] values) {
        this.values = values;
    }
}
