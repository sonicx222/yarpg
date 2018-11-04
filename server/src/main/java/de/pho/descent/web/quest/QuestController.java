package de.pho.descent.web.quest;

import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.shared.model.message.MessageType;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.monster.MonsterGroup;
import de.pho.descent.shared.model.quest.LootBox;
import static de.pho.descent.shared.model.quest.Quest.FIRST_BLOOD;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestPhase;
import de.pho.descent.shared.model.quest.QuestReward;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.web.campaign.CampaignController;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.map.MapFactory;
import de.pho.descent.web.message.MessageController;
import de.pho.descent.web.quest.encounter.FirstBlood;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
@Stateless
public class QuestController {

    private static final Logger LOG = Logger.getLogger(QuestController.class.getName());

    @Inject
    private QuestService questService;

    @Inject
    private CampaignController campaignController;

    @Inject
    private MessageController messageController;

    public QuestEncounter createQuestEncounter(QuestTemplate questTemplate, Campaign campaign, List<GameHero> heroes) throws NotFoundException, IOException, QuestValidationException {
        QuestEncounter encounter = new QuestEncounter();

        GameMap gameMap = MapFactory.createMapByTemplate(questTemplate);
        encounter.setMap(gameMap);
//        persistenceService.create(gameMap);

        encounter.getHeroes().addAll(heroes);
        encounter.setActive(true);
        encounter.setRound(1);
        encounter.setQuest(questTemplate.getQuest());
        encounter.setPart(questTemplate.getQuestPart());
        encounter.setCurrentTurn(PlaySide.HEROES);
        encounter.setPhase(QuestPhase.ACTIVE_HERO);
        encounter.setCampaign(campaign);

        switch (questTemplate) {
            case FIRST_BLOOD_INTRO:
                FirstBlood.setup(encounter, campaign.getOverlord().getPlayedBy());

                // save newly created heroes (only after first quest)
//                encounter.getHeroes().forEach(hero -> persistenceService.create(hero));
                break;
            default:
                break;
        }

        // save newly created monsters & tokens first
//        encounter.getMonsters().forEach(monster -> persistenceService.create(monster));
//        for (Token token : encounter.getToken()) {
//            LOG.info("Move Token to PersistenceContext: ID " + token.getId());
//            persistenceService.create(token);
//        }
//        encounter.getToken().forEach(token -> persistenceService.create(token));
        return questService.saveEncounter(encounter);
    }

    public void endActiveUnitTurn(QuestEncounter quest) throws QuestValidationException, NotFoundException {
        GameMonster monster = quest.getActiveMonster();
        MonsterGroup group = null;
        if (monster != null) {
            group = monster.getMonsterTemplate().getGroup();
        }
        deactivateCurrentUnit(quest);
        setNextActiveUnit(quest, group);
    }

    public QuestEncounter activateMonsterGroup(Player player, QuestEncounter quest, MonsterGroup monsterGroup) throws QuestValidationException {
        if (quest.getCurrentTurn() != PlaySide.OVERLORD) {
            throw new QuestValidationException("It is not the Overlords turn");
        }

        if (!Objects.equals(quest.getCampaign().getOverlord().getPlayedBy(), player)) {
            throw new QuestValidationException("Only the overlord can activate monster groups");
        }

        List<GameMonster> group = quest.getMonsterGroup(monsterGroup);
        if (group.isEmpty()) {
            throw new QuestValidationException("Wrong group or all monsters of group " + monsterGroup + " are dead. Nothing to activate");
        }

        List<GameMonster> monstersToActivate = new ArrayList<>();
        for (GameMonster monster : group) {
            if (monster.isActive()) {
                throw new QuestValidationException("Monster in group " + monsterGroup + " is already active!");
            }
            if (monster.getActions() > 0) {
                monstersToActivate.add(monster);
            }
        }
        if (monstersToActivate.isEmpty()) {
            throw new QuestValidationException("All monsters in group " + monsterGroup + " have no actions left");
        }

        // activate random monster from group
        monstersToActivate.get(ThreadLocalRandom.current().nextInt(0, monstersToActivate.size())).setActive(true);
        quest.setPhase(QuestPhase.ACTIVE_MONSTER);

        return quest;
    }

    public void setNextActiveUnit(QuestEncounter activeQuest, MonsterGroup activeGroup) throws QuestValidationException, NotFoundException {
        Objects.requireNonNull(activeQuest);

        List<GameMonster> activateableMonsters = activeQuest.getMonsters().stream()
                .filter(monster -> (monster.getActions() > 0
                && !monster.isRemoved()))
                .collect(Collectors.toList());
        List<GameHero> activeHeroes = activeQuest.getHeroes().stream()
                .filter(hero -> hero.getActions() > 0)
                .collect(Collectors.toList());

        // check if next unit activation is obsolete
        if (activeHeroes.isEmpty() && activateableMonsters.isEmpty()) {
            endRound(activeQuest);
            if (isActiveQuestFinished(activeQuest)) {
                endActiveQuest(activeQuest);
            }
            return;
        }

        if (activeQuest.getCurrentTurn() == PlaySide.HEROES) {
            if (activeQuest.getMonsters().isEmpty() || activateableMonsters.isEmpty()) {
                // no monsters left, all dead or played
                // set another hero as active unit
                setNextActiveHero(activeQuest);
                return;
            } else {
                // set playside: overlords turn
                activeQuest.setCurrentTurn(PlaySide.OVERLORD);
                activeQuest.setPhase(QuestPhase.MONSTER_ACTIVATION);
            }
        } else {
            if (activateableMonsters.isEmpty()) {
                setNextActiveHero(activeQuest);
                return;
            }
            if (activeQuest.getPhase() == QuestPhase.ACTIVE_MONSTER && activeGroup != null) {
                List<GameMonster> remainingGroupMonsters = activateableMonsters.stream()
                        .filter(monster -> (activeGroup == monster.getMonsterTemplate().getGroup()))
                        .collect(Collectors.toList());
                if (remainingGroupMonsters.isEmpty()) {
                    setNextActiveHero(activeQuest);
                    return;
                }
                setNextMonsterWithinGroup(activeQuest, remainingGroupMonsters);
            }
        }
    }

    private void setNextMonsterWithinGroup(QuestEncounter questEncounter, List<GameMonster> activeGroupMonsters) {
        // set random monster within same monster group
        activeGroupMonsters.get(ThreadLocalRandom.current().nextInt(0, activeGroupMonsters.size())).setActive(true);

        // set playside: overlords turn
        questEncounter.setCurrentTurn(PlaySide.OVERLORD);
    }

    private void setNextActiveHero(QuestEncounter questEncounter) {
        Objects.requireNonNull(questEncounter);

        if (!questEncounter.getHeroes().isEmpty()) {

            SortedMap<Integer, GameHero> initiativeOrder = new TreeMap<>();
            questEncounter.getHeroes().stream()
                    .filter(hero -> hero.getActions() > 0)
                    .forEach(hero -> {
                        initiativeOrder.put(hero.rollInitiative(), hero);
                    });
            GameHero nextActiveHero = initiativeOrder.get(initiativeOrder.lastKey());
            nextActiveHero.setActive(true);

            // set playside: heroes turn
            questEncounter.setCurrentTurn(PlaySide.HEROES);
            questEncounter.setPhase(QuestPhase.ACTIVE_HERO);
        }

    }

    public void deactivateCurrentUnit(QuestEncounter encounter) {
        if (encounter.getActiveHero() == null) {
            encounter.getActiveMonster().setActions(0);
            encounter.getActiveMonster().setActive(false);
        } else {
            encounter.getActiveHero().setActions(0);
            encounter.getActiveHero().setActive(false);
        }
    }

    public void updateQuestTrigger(QuestEncounter encounter) throws QuestValidationException {
        switch (encounter.getQuest()) {
            case FIRST_BLOOD:
                FirstBlood.updateQuestTrigger(encounter);
                break;
            default:
                break;
        }
    }

    public void endRound(QuestEncounter activeQuest) throws QuestValidationException {
        // increase round number
        activeQuest.setRound(activeQuest.getRound() + 1);

        // reset all action points
        activeQuest.getHeroes().stream()
                .forEach(hero -> hero.setActions(2));
        activeQuest.getMonsters().stream()
                .forEach(monster -> monster.setActions(2));

        // set random hero for new  round
        setNextActiveHero(activeQuest);

        // info message
        String msg = "Round " + activeQuest.getRound() + " started";
        messageController.saveMessage(new Message(activeQuest.getCampaign(), MessageType.GAME, null, msg));
        LOG.info(msg);
    }

    public void endActiveQuest(QuestEncounter activeEncounter) throws QuestValidationException, NotFoundException {
        LootBox box = null;

        activeEncounter.setActive(false);
        box = getQuestReward(activeEncounter);

        // rewards
        handleQuestReward(activeEncounter.getCampaign(), box);

        // next phase
        campaignController.setNextCampaignPhase(activeEncounter.getCampaign());
    }

    public LootBox getQuestReward(QuestEncounter questEncounter) {
        LootBox box = null;
        
        switch (questEncounter.getQuest()) {
            case FIRST_BLOOD: {
                box = FirstBlood.getQuestRewards();
            }
            break;
            default:
                break;
        }
        Objects.requireNonNull(box);
        
        return box;
    }

    private void handleQuestReward(Campaign campaign, LootBox box) {
        QuestEncounter encounter = campaign.getActiveQuest();

        if (encounter.getWinner() == PlaySide.HEROES) {
            QuestReward heroesReward = box.getRewardBySide(PlaySide.HEROES);

            // gold
            campaign.setGold(campaign.getGold() + heroesReward.getGold());

            // xp
            encounter.getHeroes().stream()
                    .forEach(hero -> hero.addXp(heroesReward.getXp()));

            // TODO item e.g relics
        } else {
            QuestReward overlordReward = box.getRewardBySide(PlaySide.OVERLORD);

            // xp
            campaign.getOverlord().addXp(overlordReward.getXp());

            // TODO item e.g relics
        }
    }

    public boolean isActiveQuestFinished(QuestEncounter encounter) throws QuestValidationException {
        boolean finished = false;

        switch (encounter.getQuest()) {
            case FIRST_BLOOD:
                finished = FirstBlood.isFinished(encounter);
                break;
            default:
                break;
        }

        return finished;
    }

    public QuestEncounter getQuestEncounterById(long id) {
        return questService.loadEncounterById(id);
    }

    public GameUnit getGameUnit(long unitId) throws NotFoundException {
        return questService.getGameUnitById(unitId);
    }
}
