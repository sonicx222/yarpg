package de.pho.descent.web.action.handler;

import de.pho.descent.shared.dto.WsAction;
import static de.pho.descent.shared.model.AttackType.MELEE;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.SurgeAction;
import de.pho.descent.shared.model.action.ActionType;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.dice.AttackDice;
import de.pho.descent.shared.model.dice.AttackDiceResult;
import de.pho.descent.shared.model.dice.DefenseDice;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.shared.model.message.MessageType;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.monster.ability.MonsterAbility;
import de.pho.descent.shared.service.MapLosService;
import de.pho.descent.shared.service.MapRangeService;
import de.pho.descent.web.action.ActionException;
import de.pho.descent.web.exception.Messages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author pho
 */
public class AttackHandler {

    private static final Logger LOG = Logger.getLogger(AttackHandler.class.getName());

    public static Message handle(Campaign campaign, Player player, GameUnit activeUnit, GameUnit targetUnit, WsAction wsAction) throws ActionException {
        StringBuilder sbLog = new StringBuilder(activeUnit.getName());
        sbLog.append(" attacks ").append(targetUnit.getName()).append(". ");
        StringBuilder sbMessage = new StringBuilder(sbLog.toString());

        // LOS check
        if (!MapLosService.hasLineOfSight(activeUnit, targetUnit, campaign.getActiveQuest().getMap())) {
            throw new ActionException(Messages.NO_LOS);
        }

        try {
            if (activeUnit instanceof GameMonster) {
                // handle monster attack
                handleMonsterAttack((GameMonster) activeUnit, targetUnit, campaign, sbLog, sbMessage);
            } else {
                // handle hero attack
                handleHeroAttack((GameHero) activeUnit, targetUnit, sbLog, sbMessage);
            }

            // TODO
        } catch (ActionException ae) {
            if (ae.getMessage().equals(Messages.ATTACK_FIZZLED)) {
                sbMessage.append(": Fizzled!");
                sbLog.append(" Fizzled!");
            } else if (ae.getMessage().equals(Messages.NO_RANGE_FOR_ATTACK)) {
                sbMessage.append(Messages.NO_RANGE_FOR_ATTACK);
                sbLog.append(Messages.NO_RANGE_FOR_ATTACK);
            } else {
                throw ae;
            }
        } finally {
            LOG.info(sbLog.toString());
            return new Message(campaign, MessageType.GAME, player, sbMessage.toString());
        }
    }

    private static void handleMonsterAttack(GameMonster activeMonster, GameUnit targetUnit, Campaign campaign, StringBuilder sbLog, StringBuilder sbMessage) throws ActionException {

        // check if monster attacked already on active turn
        if (activeMonster.getActions() == 1 && activeMonster.getLastAction() == ActionType.ATTACK) {
            throw new ActionException(Messages.MONSTER_ATTACK_TWICE);
        }

        // attack attributes
        int totalAttack = 0;
        int totalPierce = 0;
        int totalSurges = 0;
        int attackRange = 0;
        sbLog.append(System.lineSeparator()).append(activeMonster.getName())
                .append(" attacks with dice: ");

        // roll the attack dice
        for (AttackDice dice : activeMonster.getMonsterTemplate().getAttack()) {
            sbLog.append(dice.name()).append(" ");

            AttackDiceResult result = dice.roll();
            if (result == AttackDiceResult.FIZZLE) {
                throw new ActionException(Messages.ATTACK_FIZZLED);
            }

            totalAttack += result.getDamage();
            totalSurges += result.getSurge();
            attackRange += result.getRange();
        }

        if (activeMonster.getMonsterTemplate().getAttacktype() == MELEE) {
            attackRange = 1;
        }
        if (activeMonster.getMonsterTemplate().getAbilities().contains(MonsterAbility.REACH)) {
            attackRange = 2;
        }

        sbLog.append(System.lineSeparator())
                .append("Results: ").append(System.lineSeparator())
                .append("Damage: ").append(totalAttack).append(System.lineSeparator())
                .append("Surges: ").append(totalSurges).append(System.lineSeparator())
                .append("Range: ").append(attackRange).append(System.lineSeparator());

        // spend surges randomly if available
        if (totalSurges > 0
                && activeMonster.getMonsterTemplate().getSurgeActions() != null
                && activeMonster.getMonsterTemplate().getSurgeActions().size() > 0) {

            List<SurgeAction> surgeActions = new ArrayList<>(activeMonster.getMonsterTemplate().getSurgeActions());
            Collections.shuffle(surgeActions);

            // spend surges randomly
            sbLog.append("Spend surges randomly: ");
            int[] finalAttackResults = spendRandomSurges(totalSurges, surgeActions, totalAttack, totalPierce, attackRange, sbLog);
            totalAttack = finalAttackResults[0];
            totalPierce = finalAttackResults[1];
            attackRange = finalAttackResults[2];
        }
        sbLog.append(System.lineSeparator())
                .append("Final Attack result: ").append(System.lineSeparator())
                .append("Damage: ").append(totalAttack).append(System.lineSeparator())
                .append("Range: ").append(attackRange).append(System.lineSeparator());
        if (totalPierce > 0) {
            sbLog.append("Pierce: ").append(totalPierce).append(System.lineSeparator());
        }

        // range check
        if (!MapRangeService.isInAttackRange(activeMonster, targetUnit, attackRange, campaign.getActiveQuest().getMap().getMapFields())) {
            throw new ActionException(Messages.NO_RANGE_FOR_ATTACK);
        }
        // calc target defense
        // TODO: Interface Defender/Attacker for reuse of getDefense/getAttack of GameUnit
        GameHero hero = ((GameHero) targetUnit);
        List<DefenseDice> defense = hero.getDefense();
        int totalDefense = 0;

        // roll the defense dice
        sbLog.append(hero.getName()).append(" defends with dice: ");
        for (DefenseDice dice : defense) {
            sbLog.append(dice.name()).append(" ");

            totalDefense += dice.roll();
        }
        sbLog.append(System.lineSeparator())
                .append("Result: ").append(totalDefense).append(" total armor")
                .append(System.lineSeparator());

        // calculate damage
        // use Math.max to prevent negative values
        int damageDone = Math.max(totalAttack - Math.max(totalDefense - totalPierce, 0), 0);
        sbMessage.append("Final damage done: ").append(damageDone).append(". ");
        sbLog.append("Final damage done: ").append(damageDone)
                .append(System.lineSeparator());

        handleDamageDoneToUnit(targetUnit, damageDone, campaign, sbLog, sbMessage);
    }

    private static void handleHeroAttack(GameHero activeHero, GameUnit targetUnit, StringBuilder sbLog, StringBuilder sbMessage) {

    }

    private static void handleDamageDoneToUnit(GameUnit targetUnit, int damageDone, Campaign campaign, StringBuilder sbLog, StringBuilder sbMessage) {
        // do damage
        targetUnit.setCurrentLife(targetUnit.getCurrentLife() - damageDone);

        if (targetUnit.getCurrentLife() <= 0) {
            if (targetUnit instanceof GameMonster) {
                campaign.getActiveQuest().getMonsters().remove((GameMonster) targetUnit);
                sbMessage.append(targetUnit.getName()).append(" is dead. ");
                sbLog.append(targetUnit.getName()).append(" is dead. ");
            } else {
                // hero is knocked out
                ((GameHero) targetUnit).knockOutHero();
                sbMessage.append(targetUnit.getName()).append(" has been knocked out! ");
                sbLog.append(targetUnit.getName()).append(" has been knocked out! ");
                // TODO: overlord player draws immediately one overlord card
            }
        } else {
            sbMessage.append(targetUnit.getName()).append(" now has ")
                    .append(targetUnit.getCurrentLife()).append(" hp left");
            sbLog.append(targetUnit.getName()).append(" now has ")
                    .append(targetUnit.getCurrentLife()).append(" hp left");
        }
    }

    private static int[] spendRandomSurges(int totalSurges, List<SurgeAction> surgeActions, int totalAttack, int totalPierce, int attackRange, StringBuilder sbLog) {
        int[] results = new int[3];
        results[0] = totalAttack;
        results[1] = totalPierce;
        results[2] = attackRange;

        for (int i = 0; i < totalSurges && i < surgeActions.size(); i++) {
            SurgeAction surgeAction = surgeActions.get(i);
            sbLog.append(surgeAction.name()).append(" ");

            switch (surgeAction) {
                case ONE_DAMAGE:
                    results[0] = totalAttack + 1;
                    break;
                case TWO_DAMAGE:
                    results[0] = totalAttack + 2;
                    break;
                case THREE_DAMAGE:
                    results[0] = totalAttack + 3;
                    break;
                case ONE_PIERCE:
                    results[1] = totalPierce + 1;
                    break;
                case TWO_PIERCE:
                    results[1] = totalPierce + 2;
                    break;
                case THREE_PIERCE:
                    results[1] = totalPierce + 3;
                    break;
                case ONE_RANGE:
                    results[2] = attackRange + 1;
                    break;
                case TWO_RANGE:
                    results[2] = attackRange + 2;
                    break;
                case THREE_RANGE:
                    results[2] = attackRange + 3;
                    break;
            }
        }

        return results;
    }
}
