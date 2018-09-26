package de.pho.descent.web.quest;

import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.map.MapController;
import de.pho.descent.web.quest.encounter.FirstBlood;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
@Stateless
public class QuestController {

    @Inject
    private QuestService questService;

    @Inject
    private MapController mapController;

    public QuestEncounter getQuestEncounterById(long id) {
        return questService.loadEncounterById(id);
    }

    public QuestEncounter startNextQuestEncounter(Campaign campaign) throws NotFoundException {
        return createQuestEncounter(campaign.getTemplateNextQuest(), campaign);
    }

    public QuestEncounter createQuestEncounter(QuestTemplate questTemplate, Campaign campaign) throws NotFoundException {
        QuestEncounter encounter = new QuestEncounter();

        GameMap gameMap = mapController.getMapByQuestTemplate(questTemplate);
        encounter.setActive(true);
        encounter.setRound(1);
        encounter.setMap(gameMap);
        encounter.setQuest(questTemplate.getQuest());
        encounter.setPart(questTemplate.getQuestPart());
        encounter.setCurrentTurn(PlaySide.HEROES);

        // set first active hero based on highest initiative roll
        if (!campaign.getHeroes().isEmpty()) {
            SortedMap<Integer, GameHero> initiativeOrder = new TreeMap<>();
            campaign.getHeroes().stream().forEach(hero -> {
                initiativeOrder.put(hero.rollInitiative(), hero);
            });
            GameHero activeHero = initiativeOrder.get(initiativeOrder.lastKey());
            activeHero.setActive(true);
        }

        switch (questTemplate) {
            case FIRST_BLOOD_INTRO:
                FirstBlood.setup(encounter, campaign);
                break;
            default:
                break;
        }

        return questService.saveEncounter(encounter);
    }

    public boolean isActiveQuestFinished(Campaign campaign) throws QuestValidationException {
        QuestEncounter encounter = campaign.getActiveQuest();
        boolean finished = false;

        switch (encounter.getQuest()) {
            case FIRST_BLOOD:
                finished = FirstBlood.checkState(encounter);
                if (finished) {
                    FirstBlood.endQuest(campaign);

                    // next phase
                    campaign.setPhase(CampaignPhase.MARKETPLACE);
                }
                break;
            default:
                break;
        }

        return finished;
    }
    

    public void setNextActiveHero(Campaign campaign) {
        Objects.requireNonNull(campaign);

        if (!campaign.getHeroes().isEmpty()) {
            deactivateCurrentUnit(campaign);

            SortedMap<Integer, GameHero> initiativeOrder = new TreeMap<>();
            campaign.getHeroes().stream()
                    .filter(hero -> hero.getActions() > 0)
                    .forEach(hero -> {
                        initiativeOrder.put(hero.rollInitiative(), hero);
                    });
            GameHero nextActiveHero = initiativeOrder.get(initiativeOrder.lastKey());
            nextActiveHero.setActive(true);

            // set playside: heroes turn
            campaign.getActiveQuest().setCurrentTurn(PlaySide.HEROES);
        }

    }

    public void setNextActiveUnit(Campaign campaign) throws QuestValidationException {
        Objects.requireNonNull(campaign);
        QuestEncounter activeQuest = campaign.getActiveQuest();
        List<GameMonster> activeMonsters = activeQuest.getMonsters().stream()
                .filter(monster -> monster.getActions() > 0)
                .collect(Collectors.toList());
        List<GameHero> activeHeroes = campaign.getHeroes().stream()
                .filter(hero -> hero.getActions() > 0)
                .collect(Collectors.toList());

        if (activeHeroes.isEmpty() && activeMonsters.isEmpty()) {
            endRound(campaign);
            return;
        }

        if (activeQuest.getCurrentTurn() == PlaySide.HEROES) {

            if (activeQuest.getMonsters().isEmpty() || activeMonsters.isEmpty()) {
                // no monsters left, all dead or played
                // set another hero as active unit
                setNextActiveHero(campaign);
                return;
            } else {
                // unflag active hero
                campaign.getActiveHero().setActive(false);

                // set random monster as new active unit and activate monster group
                activeMonsters.get(ThreadLocalRandom.current().nextInt(0, activeMonsters.size())).setActive(true);

                // set playside: overlords turn
                campaign.getActiveQuest().setCurrentTurn(PlaySide.OVERLORD);
            }
        } else {
            GameMonster currentActiveMonster = activeQuest.getActiveMonster();
            List<GameMonster> activeGroupMonsters = activeQuest.getMonsters().stream()
                    .filter(monster -> (monster.getActions() > 0)
                    && (currentActiveMonster.getMonsterTemplate().getGroup() == monster.getMonsterTemplate().getGroup()))
                    .collect(Collectors.toList());

            if (activeMonsters.isEmpty() || activeGroupMonsters.isEmpty()) {
                setNextActiveHero(campaign);
                return;
            }

            if (activeGroupMonsters.isEmpty() && activeHeroes.isEmpty()) {
                // switch to next monster group
                List<GameMonster> activeNonGroupMonsters = activeQuest.getMonsters().stream()
                        .filter(monster -> (monster.getActions() > 0)
                        && (currentActiveMonster.getMonsterTemplate().getGroup() != monster.getMonsterTemplate().getGroup()))
                        .collect(Collectors.toList());

                setNextMonster(campaign, currentActiveMonster, activeNonGroupMonsters);
                return;
            }

            setNextMonster(campaign, currentActiveMonster, activeGroupMonsters);
        }
    }

    private void setNextMonster(Campaign campaign, GameMonster currentActiveMonster, List<GameMonster> monsters) {
        // unflag active monster
        currentActiveMonster.setActive(false);

        // set random monster within same monster group
        monsters.get(ThreadLocalRandom.current().nextInt(0, monsters.size())).setActive(true);

        // set playside: overlords turn
        campaign.getActiveQuest().setCurrentTurn(PlaySide.OVERLORD);
    }

    private void deactivateCurrentUnit(Campaign campaign) {
        if (campaign.getActiveHero() == null) {
            campaign.getActiveQuest().getActiveMonster().setActive(false);
        } else {
            campaign.getActiveHero().setActive(false);
        }
    }
    
    public void updateQuestTrigger(QuestEncounter encounter) {
        switch (encounter.getQuest()) {
            case FIRST_BLOOD:
                FirstBlood.updateQuestTrigger(encounter);
                break;
            default:
                break;
        }
    }

    public void endRound(Campaign campaign) throws QuestValidationException {

        QuestEncounter activeQuest = campaign.getActiveQuest();
        // increase round number
        activeQuest.setRound(activeQuest.getRound() + 1);

        // check victory conditions for specific quest round timer
        if (!isActiveQuestFinished(campaign)) {
            // reset all action points
            campaign.getHeroes().stream()
                    .forEach(hero -> hero.setActions(2));
            activeQuest.getMonsters().stream()
                    .forEach(monster -> monster.setActions(2));

            // set random hero for new  round
            setNextActiveHero(campaign);
        }
    }

}
