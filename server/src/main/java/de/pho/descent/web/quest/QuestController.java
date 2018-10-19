package de.pho.descent.web.quest;

import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.hero.HeroSelection;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.web.campaign.CampaignController;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.map.MapFactory;
import de.pho.descent.web.quest.encounter.FirstBlood;
import de.pho.descent.web.service.PersistenceService;
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
    private transient PersistenceService persistenceService;



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

    public void setNextActiveHero(QuestEncounter questEncounter) {
        Objects.requireNonNull(questEncounter);

        if (!questEncounter.getHeroes().isEmpty()) {
            deactivateCurrentUnit(questEncounter);

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
        }

    }

    public void setNextActiveUnit(Campaign campaign) throws QuestValidationException, NotFoundException {
        QuestEncounter activeQuest = campaign.getActiveQuest();
        Objects.requireNonNull(activeQuest);

        List<GameMonster> activeMonsters = activeQuest.getMonsters().stream()
                .filter(monster -> monster.getActions() > 0)
                .collect(Collectors.toList());
        List<GameHero> activeHeroes = activeQuest.getHeroes().stream()
                .filter(hero -> hero.getActions() > 0)
                .collect(Collectors.toList());

        if (activeHeroes.isEmpty() && activeMonsters.isEmpty()) {
            endRound(activeQuest);
            if (isActiveQuestFinished(activeQuest)) {
                campaignController.endActiveQuest(campaign);
            }
            return;
        }

        if (activeQuest.getCurrentTurn() == PlaySide.HEROES) {

            if (activeQuest.getMonsters().isEmpty() || activeMonsters.isEmpty()) {
                // no monsters left, all dead or played
                // set another hero as active unit
                setNextActiveHero(activeQuest);
                return;
            } else {
                // unflag active hero
                activeQuest.getActiveHero().setActive(false);

                // set random monster as new active unit and activate monster group
                activeMonsters.get(ThreadLocalRandom.current().nextInt(0, activeMonsters.size())).setActive(true);

                // set playside: overlords turn
                activeQuest.setCurrentTurn(PlaySide.OVERLORD);
            }
        } else {
            GameMonster currentActiveMonster = activeQuest.getActiveMonster();
            List<GameMonster> activeGroupMonsters = activeQuest.getMonsters().stream()
                    .filter(monster -> (monster.getActions() > 0)
                    && (currentActiveMonster.getMonsterTemplate().getGroup() == monster.getMonsterTemplate().getGroup()))
                    .collect(Collectors.toList());

            if (activeMonsters.isEmpty() || activeGroupMonsters.isEmpty()) {
                setNextActiveHero(activeQuest);
                return;
            }

            if (activeGroupMonsters.isEmpty() && activeHeroes.isEmpty()) {
                // switch to next monster group
                List<GameMonster> activeNonGroupMonsters = activeQuest.getMonsters().stream()
                        .filter(monster -> (monster.getActions() > 0)
                        && (currentActiveMonster.getMonsterTemplate().getGroup() != monster.getMonsterTemplate().getGroup()))
                        .collect(Collectors.toList());

                setNextMonster(activeQuest, activeNonGroupMonsters);
                return;
            }

            setNextMonster(activeQuest, activeGroupMonsters);
        }
    }

    private void setNextMonster(QuestEncounter questEncounter, List<GameMonster> activeGroupMonsters) {
        // unflag active monster
        questEncounter.getActiveMonster().setActive(false);

        // set random monster within same monster group
        activeGroupMonsters.get(ThreadLocalRandom.current().nextInt(0, activeGroupMonsters.size())).setActive(true);

        // set playside: overlords turn
        questEncounter.setCurrentTurn(PlaySide.OVERLORD);
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
