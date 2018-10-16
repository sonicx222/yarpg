package de.pho.descent.web.action.handler;

import de.pho.descent.shared.dto.WsAction;
import static de.pho.descent.shared.model.AttackType.MELEE;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.SurgeAction;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.dice.AttackDice;
import de.pho.descent.shared.model.dice.AttackDiceResult;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.shared.model.message.MessageType;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.monster.ability.MonsterAbility;
import de.pho.descent.web.action.ActionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author pho
 */
public class AttackHandler {

    public static Message handle(Campaign campaign, Player player, GameUnit activeUnit, WsAction wsAction) throws ActionException {
        StringBuilder sbLog = new StringBuilder();
        GameHero activeHero = null;
        GameMonster activeMonster = null;

        // attack attributes
        int totalAttack = 0;
        int totalPierce = 0;
        int totalSurges = 0;
        int weaponRange = 0;
        
        // TODO: LOS check!

        if (activeUnit instanceof GameMonster) {
            // handle monster attack
            activeMonster = (GameMonster) activeUnit;

            // roll the dice
            for (AttackDice dice : activeMonster.getMonsterTemplate().getAttack()) {
                AttackDiceResult result = dice.roll();
                totalAttack += result.getDamage();
                totalSurges += result.getSurge();
                weaponRange += result.getRange();
            }

            if (activeMonster.getMonsterTemplate().getAttacktype() == MELEE) {
                weaponRange = 1;
            } else if (activeMonster.getMonsterTemplate().getAbilities().contains(MonsterAbility.REACH)) {
                weaponRange = 2;
            }

            // spend surges randomly if available
            if (totalSurges > 0
                    && activeMonster.getMonsterTemplate().getSurgeActions() != null
                    && activeMonster.getMonsterTemplate().getSurgeActions().size() > 0) {

                List<SurgeAction> surgeActions = new ArrayList<>(activeMonster.getMonsterTemplate().getSurgeActions());
                Collections.shuffle(surgeActions);

                // spend surges randomly
                for (int i = 0; i < totalSurges && i < surgeActions.size(); i++) {
                    SurgeAction surgeAction = surgeActions.get(i);
                    switch (surgeAction) {
                        case ONE_DAMAGE:
                            totalAttack += 1;
                            break;
                        case TWO_DAMAGE:
                            totalAttack += 2;
                            break;
                        case THREE_DAMAGE:
                            totalAttack += 3;
                            break;
                        case ONE_PIERCE:
                            totalPierce += 1;
                            break;
                        case TWO_PIERCE:
                            totalPierce += 2;
                            break;
                        case THREE_PIERCE:
                            totalPierce += 3;
                            break;
                        case ONE_RANGE:
                            weaponRange += 1;
                            break;
                        case TWO_RANGE:
                            weaponRange += 2;
                            break;
                        case THREE_RANGE:
                            weaponRange += 3;
                            break;
                    }
                }
            }
            
            // TODO: range check
            
            
        } else {
            // handle hero attack
            activeHero = (GameHero) activeUnit;
        }

        // TODO
        return new Message(campaign, MessageType.GAME, player, sbLog.toString());
    }

}
