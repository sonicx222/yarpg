package de.pho.descent.web.action.handler;

import de.pho.descent.shared.dto.WsAction;
import static de.pho.descent.shared.model.AttackType.MELEE;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.SurgeAction;
import de.pho.descent.shared.model.action.ActionType;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.dice.AttackDice;
import de.pho.descent.shared.model.dice.AttackDiceResult;
import de.pho.descent.shared.model.dice.DefenseDice;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.map.MapField;
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

    public static Message handle(Campaign campaign, GameUnit activeUnit, GameUnit targetUnit, WsAction wsAction) throws ActionException {
        StringBuilder sbLog = new StringBuilder(activeUnit.getName());
        sbLog.append(" attacks ").append(targetUnit.getName()).append(". ");
        StringBuilder sbMessage = new StringBuilder(sbLog.toString());

        GameMap map = campaign.getActiveQuest().getMap();
        // LOS check
        if (!MapLosService.hasLineOfSight(activeUnit, targetUnit, map)) {
            sbMessage.append(Messages.NO_LOS);
            return new Message(campaign, MessageType.GAME, activeUnit.getPlayedBy(), sbMessage.toString());
        }

        // attack attributes
        // 0 = damage
        // 1 = surges
        // 2 = range
        // 3 = pierce
        int[] attackAttributes = new int[4];
        Message ingameMessage = null;

        if (activeUnit instanceof GameMonster) {
            // handle monster attack
            ingameMessage = handleMonsterAttack((GameMonster) activeUnit, targetUnit, attackAttributes, campaign, sbLog, sbMessage);
        } else {
            // handle hero attack
            ingameMessage = handleHeroAttack((GameHero) activeUnit, targetUnit, attackAttributes, campaign, sbLog, sbMessage);
        }

        // used one action point to handle & set last action
        activeUnit.setActions(activeUnit.getActions() - 1);
        activeUnit.setLastAction(ActionType.ATTACK);

        LOG.info(sbLog.toString());
        return ingameMessage;
    }

    private static Message handleMonsterAttack(GameMonster activeMonster, GameUnit targetUnit, int[] attackAttributes, Campaign campaign, StringBuilder sbLog, StringBuilder sbMessage) throws ActionException {

        // check if monster attacked already on active turn
        if (activeMonster.getActions() == 1 && activeMonster.getLastAction() == ActionType.ATTACK) {
            throw new ActionException(Messages.MONSTER_ATTACK_TWICE);
        }

        // roll the attack dice
        sbLog.append(System.lineSeparator()).append(activeMonster.getName())
                .append(" attacks with dice: ");
        for (AttackDice dice : activeMonster.getMonsterTemplate().getAttack()) {
            AttackDiceResult result = dice.roll();
            sbLog.append(dice.name()).append(" ");
            
            if (result == AttackDiceResult.FIZZLE) {
                sbMessage.append(": Fizzled!");
                sbLog.append(" Fizzled!");
                return new Message(campaign, MessageType.GAME, activeMonster.getPlayedBy(), sbMessage.toString());
            }

            attackAttributes[0] += result.getDamage();
            attackAttributes[1] += result.getSurge();
            attackAttributes[2] += result.getRange();
        }

        // set correct max range
        if (activeMonster.getMonsterTemplate().getAttackType() == MELEE) {
            attackAttributes[0] = 1;
        }
        if (activeMonster.getMonsterTemplate().getAbilities().contains(MonsterAbility.REACH)) {
            attackAttributes[0] = 2;
        }

        sbLog.append(System.lineSeparator())
                .append("Results: ").append(System.lineSeparator())
                .append("Damage: ").append(attackAttributes[0]).append(System.lineSeparator())
                .append("Surges: ").append(attackAttributes[1]).append(System.lineSeparator())
                .append("Range: ").append(attackAttributes[2]).append(System.lineSeparator());

        // spend surges randomly if available
        spendRandomSurges(attackAttributes, activeMonster.getMonsterTemplate().getSurgeActions(), sbLog);

        sbLog.append(System.lineSeparator())
                .append("Final Attack result: ").append(System.lineSeparator())
                .append("Damage: ").append(attackAttributes[0]).append(System.lineSeparator())
                .append("Range: ").append(attackAttributes[2]).append(System.lineSeparator());
        if (attackAttributes[3] > 0) {
            sbLog.append("Pierce: ").append(attackAttributes[3]).append(System.lineSeparator());
        }

        // range check
        if (!MapRangeService.isInAttackRange(activeMonster, targetUnit, attackAttributes[2], campaign.getActiveQuest().getMap().getMapFields())) {
            sbMessage.append(Messages.NO_RANGE_FOR_ATTACK);
            sbLog.append(Messages.NO_RANGE_FOR_ATTACK);
            return new Message(campaign, MessageType.GAME, activeMonster.getPlayedBy(), sbMessage.toString());
        }

        if (targetUnit instanceof GameMonster) {
            handleDamageDoneToMonster((GameMonster) targetUnit, attackAttributes, sbLog, sbMessage);
        } else {
            handleDamageDoneToHero((GameHero) targetUnit, attackAttributes, sbLog, sbMessage);
        }

        return new Message(campaign, MessageType.GAME, activeMonster.getPlayedBy(), sbMessage.toString());
    }

    private static Message handleHeroAttack(GameHero activeHero, GameUnit targetUnit, int[] attackAttributes, Campaign campaign, StringBuilder sbLog, StringBuilder sbMessage) {
        sbLog.append(System.lineSeparator()).append(activeHero.getName())
                .append(" attacks with dice: ");

        // roll the attack dice
        for (AttackDice dice : activeHero.getWeapon().getAttackDice()) {
            sbLog.append(dice.name()).append(" ");

            AttackDiceResult result = dice.roll();
            if (result == AttackDiceResult.FIZZLE) {
                sbMessage.append(": Fizzled!");
                sbLog.append(" Fizzled!");
                return new Message(campaign, MessageType.GAME, activeHero.getPlayedBy(), sbMessage.toString());
            }

            attackAttributes[0] += result.getDamage();
            attackAttributes[1] += result.getSurge();
            attackAttributes[2] += result.getRange();
        }

        // set correct max range
        switch (activeHero.getWeapon().getAttackType()) {
            case MELEE:
                attackAttributes[0] = 1;
                break;
            case RANGED_MELEE:
                attackAttributes[0] = 2;
                break;
        }

        sbLog.append(System.lineSeparator())
                .append("Results: ").append(System.lineSeparator())
                .append("Damage: ").append(attackAttributes[0]).append(System.lineSeparator())
                .append("Surges: ").append(attackAttributes[1]).append(System.lineSeparator())
                .append("Range: ").append(attackAttributes[2]).append(System.lineSeparator());

        // spend surges randomly if available
        spendRandomSurges(attackAttributes, activeHero.getWeapon().getSurgeActions(), sbLog);

        sbLog.append(System.lineSeparator())
                .append("Final Attack result: ").append(System.lineSeparator())
                .append("Damage: ").append(attackAttributes[0]).append(System.lineSeparator())
                .append("Range: ").append(attackAttributes[2]).append(System.lineSeparator());
        if (attackAttributes[3] > 0) {
            sbLog.append("Pierce: ").append(attackAttributes[3]).append(System.lineSeparator());
        }

        // range check
        if (!MapRangeService.isInAttackRange(activeHero, targetUnit, attackAttributes[2], campaign.getActiveQuest().getMap().getMapFields())) {
            sbMessage.append(Messages.NO_RANGE_FOR_ATTACK);
            sbLog.append(Messages.NO_RANGE_FOR_ATTACK);
            return new Message(campaign, MessageType.GAME, activeHero.getPlayedBy(), sbMessage.toString());
        }

        if (targetUnit instanceof GameMonster) {
            handleDamageDoneToMonster((GameMonster) targetUnit, attackAttributes, sbLog, sbMessage);
        } else {
            handleDamageDoneToHero((GameHero) targetUnit, attackAttributes, sbLog, sbMessage);
        }

        return new Message(campaign, MessageType.GAME, activeHero.getPlayedBy(), sbMessage.toString());
    }

    private static void handleDamageDoneToMonster(GameMonster targetMonster, int[] attackAttributes, StringBuilder sbLog, StringBuilder sbMessage) {
        // calc target defense
        // TODO: Interface Defender/Attacker for reuse of getDefense/getAttack of GameUnit
        int totalDefense = 0;

        // roll the defense dice
        sbLog.append(targetMonster.getName()).append(" defends with dice: ");
        for (DefenseDice dice : targetMonster.getMonsterTemplate().getDefense()) {
            sbLog.append(dice.name()).append(" ");

            totalDefense += dice.roll();
        }
        sbLog.append(System.lineSeparator())
                .append("Result: ").append(totalDefense).append(" total armor")
                .append(System.lineSeparator());

        // do damage
        int damageDone = calcDamage(attackAttributes, totalDefense, sbLog, sbMessage);
        targetMonster.setCurrentLife(targetMonster.getCurrentLife() - damageDone);

        if (targetMonster.getCurrentLife() <= 0) {
            targetMonster.setRemoved(true);
            targetMonster.getCurrentLocation().forEach(field -> field.setGameUnit(null));
            targetMonster.getCurrentLocation().clear();
            
            sbMessage.append(targetMonster.getName()).append(" is dead. ");
            sbLog.append(targetMonster.getName()).append(" is dead. ");
        } else {
            sbMessage.append(targetMonster.getName()).append(" now has ")
                    .append(targetMonster.getCurrentLife()).append(" hp left");
            sbLog.append(targetMonster.getName()).append(" now has ")
                    .append(targetMonster.getCurrentLife()).append(" hp left");
        }
    }

    private static void handleDamageDoneToHero(GameHero targetHero, int[] attackAttributes, StringBuilder sbLog, StringBuilder sbMessage) {
        // calc target defense
        // TODO: Interface Defender/Attacker for reuse of getDefense/getAttack of GameUnit
        List<DefenseDice> defense = targetHero.getDefense();
        int totalDefense = 0;

        // roll the defense dice
        sbLog.append(targetHero.getName()).append(" defends with dice: ");
        for (DefenseDice dice : defense) {
            sbLog.append(dice.name()).append(" ");

            totalDefense += dice.roll();
        }
        sbLog.append(System.lineSeparator())
                .append("Result: ").append(totalDefense).append(" total armor")
                .append(System.lineSeparator());

        // do damage
        int damageDone = calcDamage(attackAttributes, totalDefense, sbLog, sbMessage);
        targetHero.setCurrentLife(targetHero.getCurrentLife() - damageDone);

        // check hero state
        if (targetHero.getCurrentLife() <= 0) {
            targetHero.knockOutHero();
            
            sbMessage.append(targetHero.getName()).append(" has been knocked out! ");
            sbLog.append(targetHero.getName()).append(" has been knocked out! ");
            // TODO: overlord player draws immediately one overlord card
        } else {
            sbMessage.append(targetHero.getName()).append(" now has ")
                    .append(targetHero.getCurrentLife()).append(" hp left");
            sbLog.append(targetHero.getName()).append(" now has ")
                    .append(targetHero.getCurrentLife()).append(" hp left");
        }
    }

    private static void spendRandomSurges(int[] attackAttributes, List<SurgeAction> surgeActions, StringBuilder sbLog) {
        if (attackAttributes[1] > 0 && surgeActions != null && surgeActions.size() > 0) {
            List<SurgeAction> randomActions = new ArrayList<>(surgeActions);
            Collections.shuffle(randomActions);

            // spend surges randomly
            sbLog.append("Spend surges randomly: ");

            for (int i = 0; i < attackAttributes[1] && i < surgeActions.size(); i++) {
                SurgeAction surgeAction = surgeActions.get(i);
                sbLog.append(surgeAction.name()).append(" ");

                switch (surgeAction) {
                    case ONE_DAMAGE:
                        attackAttributes[0] += 1;
                        break;
                    case TWO_DAMAGE:
                        attackAttributes[0] += 2;
                        break;
                    case THREE_DAMAGE:
                        attackAttributes[0] += 3;
                        break;
                    case ONE_PIERCE:
                        attackAttributes[3] += 1;
                        break;
                    case TWO_PIERCE:
                        attackAttributes[3] += 2;
                        break;
                    case THREE_PIERCE:
                        attackAttributes[3] += 3;
                        break;
                    case ONE_RANGE:
                        attackAttributes[2] += 1;
                        break;
                    case TWO_RANGE:
                        attackAttributes[2] += 2;
                        break;
                    case THREE_RANGE:
                        attackAttributes[2] += 3;
                        break;
                }
            }
        }
    }

    private static int calcDamage(int[] attackAttributes, int totalDefense, StringBuilder sbLog, StringBuilder sbMessage) {
        // calculate damage: use Math.max to prevent negative values
        // attackAttributes[0] = damage, attackAttributes[3] = pierce
        int damageDone = Math.max(attackAttributes[0] - Math.max(totalDefense - attackAttributes[3], 0), 0);

        sbMessage.append("Final damage done: ").append(damageDone).append(". ");
        sbLog.append("Final damage done: ").append(damageDone)
                .append(System.lineSeparator());

        return damageDone;
    }
}
