package de.pho.descent.web.quest.encounter;

import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.monster.MonsterGroup;
import static de.pho.descent.shared.model.monster.MonsterGroup.*;
import static de.pho.descent.shared.model.monster.MonsterTemplate.*;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.shared.model.token.Token;
import de.pho.descent.shared.model.token.TokenType;
import de.pho.descent.web.quest.QuestValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 *
 * @author pho
 */
public class FirstBlood {

    private static final QuestTemplate template = QuestTemplate.FIRST_BLOOD_INTRO;
    private static final String MAULER = "Mauler";

    public static void setup(QuestEncounter encounter, Campaign campaign) {
        spawnHeroes(encounter, campaign);
        spawnMonsters(encounter, campaign);
        spawnToken(encounter, campaign);
    }

    public static boolean checkState(QuestEncounter encounter) throws QuestValidationException {
        boolean finished = false;

        // validate
        validateQuest(encounter);

        if (encounter.getCurrentTurn() == PlaySide.HEROES && getMauler(encounter).getCurrentLife() <= 0) {
            // Heores win
            // handle end of quest and next phase (marketplace)
            encounter.setWinner(PlaySide.HEROES);
            finished = true;
        }

        if (encounter.getCurrentTurn() == PlaySide.OVERLORD && encounter.getTrigger() >= 5) {
            // Overlord wins
            // handle end of quest and next phase (marketplace)
            encounter.setWinner(PlaySide.OVERLORD);
            finished = true;
        }

        return finished;
    }

    public static void updateQuestTrigger(QuestEncounter encounter) {

        List<GameMonster> goblins = encounter.getMonsters().stream()
                .filter(monster -> (monster.getMonsterTemplate() == GOBLIN_ARCHER_NORMAL_ACT1
                || monster.getMonsterTemplate() == GOBLIN_ARCHER_ELITE_ACT1))
                .filter(monster -> monster.getCurrentLocation().get(0).getTileGroup().getName().equals("EXITA"))
                .collect(Collectors.toList());

        // update amount of left goblins
        encounter.setTrigger(encounter.getTrigger() + goblins.size());

        // remove specific goblins from map
        encounter.getMonsters().removeAll(goblins);
    }

    public static void endQuest(Campaign campaign) throws QuestValidationException {
        // validate
        validateQuest(campaign.getActiveQuest());
        campaign.getActiveQuest().setActive(false);

        // rewards 1 xp for overlord
        campaign.getOverlord().addXp(1);

        // rewards 1 xp for each hero
        campaign.getHeroes().stream()
                .forEach(hero -> hero.addXp(1));
    }

    private static Map<MonsterGroup, List<GameMonster>> createMonsters(Campaign campaign) {
        Map<MonsterGroup, List<GameMonster>> monsterMap = new HashMap<>();
        int heroCount = campaign.getHeroes().size();

        // goblin archers
        monsterMap.put(GOBLIN_ARCHER, new ArrayList<GameMonster>());
        for (int i = 0; i < GOBLIN_ARCHER.getNormalCount(heroCount); i++) {
            GameMonster gameMonster = new GameMonster(GOBLIN_ARCHER_NORMAL_ACT1);
            gameMonster.setPlayedBy(campaign.getOverlord().getPlayedBy());
            monsterMap.get(GOBLIN_ARCHER).add(gameMonster);
        }
        if (GOBLIN_ARCHER.hasElite(heroCount)) {
            GameMonster gameMonster = new GameMonster(GOBLIN_ARCHER_ELITE_ACT1);
            gameMonster.setName("Elite " + gameMonster.getName());
            gameMonster.setPlayedBy(campaign.getOverlord().getPlayedBy());
            monsterMap.get(GOBLIN_ARCHER).add(gameMonster);
        }

        // ettins
        monsterMap.put(ETTIN, new ArrayList<GameMonster>());
        for (int i = 0; i < ETTIN.getNormalCount(heroCount); i++) {
            GameMonster gameMonster = new GameMonster(ETTIN_NORMAL_ACT1);
            gameMonster.setPlayedBy(campaign.getOverlord().getPlayedBy());
            monsterMap.get(ETTIN).add(gameMonster);
        }
        if (ETTIN.hasElite(heroCount)) {
            GameMonster gameMonster = new GameMonster(ETTIN_ELITE_ACT1);
            gameMonster.setName("Elite " + gameMonster.getName());
            gameMonster.setPlayedBy(campaign.getOverlord().getPlayedBy());
            monsterMap.get(ETTIN).add(gameMonster);
        }

        // create Mauler
        int randomIndex = ThreadLocalRandom.current().nextInt(0, monsterMap.get(ETTIN).size());
        GameMonster ettin = monsterMap.get(ETTIN).get(randomIndex);

        // Mauler has 2hp more for each hero
        ettin.setTotalLife(ettin.getTotalLife() + (2 * heroCount));
        ettin.setCurrentLife(ettin.getTotalLife());
        ettin.setName(MAULER);

        return monsterMap;
    }

    private static void spawnHeroes(QuestEncounter encounter, Campaign campaign) {
        List<MapField> heroSpawnFields = encounter
                .getMap().getHeroSpawnFields().stream()
                .filter(field -> field.getTileGroup().getName().equals("EXITA"))
                .collect(Collectors.toList());
        // random spawn order
        Collections.shuffle(heroSpawnFields);

        int i = 0;
        for (GameHero hero : campaign.getHeroes()) {
            MapField heroSpawnField = heroSpawnFields.get(i);
            hero.getCurrentLocation().add(heroSpawnField);
            heroSpawnField.setGameUnit(hero);
            i++;
        }
    }

    private static void spawnMonsters(QuestEncounter encounter, Campaign campaign) {
        // create monsters first
        Map<MonsterGroup, List<GameMonster>> monsterMap = createMonsters(campaign);

        // spawn goblins
        List<MapField> goblinSpawnFields = encounter
                .getMap().getMonsterSpawnFields().stream()
                .filter(field -> field.getTileGroup().getName().equals("26A"))
                .collect(Collectors.toList());
        // random spawn order
        Collections.shuffle(goblinSpawnFields);

        for (int i = 0; i < monsterMap.get(GOBLIN_ARCHER).size(); i++) {
            GameMonster goblin = monsterMap.get(GOBLIN_ARCHER).get(i);
            MapField monsterSpawnField = goblinSpawnFields.get(i);
            goblin.getCurrentLocation().add(monsterSpawnField);
            monsterSpawnField.setGameUnit(goblin);
            encounter.getMonsters().add(goblin);
        }

        // spawn ettins
        List<MapField> ettinSpawnFields = encounter
                .getMap().getMonsterSpawnFields().stream()
                .filter(field -> (field.getxPos() == 5 || field.getxPos() == 7)
                && field.getyPos() == 0)
                .collect(Collectors.toList());
        // random spawn order
        Collections.shuffle(ettinSpawnFields);

        for (int i = 0; i < monsterMap.get(ETTIN).size(); i++) {
            GameMonster ettin = monsterMap.get(ETTIN).get(i);
            MapField monsterSpawnField = ettinSpawnFields.get(i);
            ettin.getCurrentLocation().add(monsterSpawnField);
            monsterSpawnField.setGameUnit(ettin);

            MapField east = encounter.getMap().getField(monsterSpawnField.getxPos() + 1, monsterSpawnField.getyPos());
            ettin.getCurrentLocation().add(east);
            east.setGameUnit(ettin);

            MapField southeast = encounter.getMap().getField(monsterSpawnField.getxPos() + 1, monsterSpawnField.getyPos() + 1);
            ettin.getCurrentLocation().add(southeast);
            southeast.setGameUnit(ettin);

            MapField south = encounter.getMap().getField(monsterSpawnField.getxPos(), monsterSpawnField.getyPos() + 1);
            ettin.getCurrentLocation().add(south);
            south.setGameUnit(ettin);

            encounter.getMonsters().add(ettin);
        }
    }

    private static List<Token> createToken(Campaign campaign) {
        List<Token> searchTokens = new ArrayList<>();
        int heroCount = campaign.getHeroes().size();

        for (int i = 0; i < heroCount; i++) {
            searchTokens.add(new Token(TokenType.SEARCH, true));
        }

        return searchTokens;
    }

    private static void spawnToken(QuestEncounter encounter, Campaign campaign) {
        encounter.getToken().clear();

        // create search tokens to be placed
        List<Token> searchTokens = createToken(campaign);

        for (int i = 0; i < searchTokens.size(); i++) {
            Token searchToken = searchTokens.get(i);
            MapField field = null;
            switch (i) {
                case 0:
                    field = encounter.getMap().getField(4, 9);
                    searchToken.setCurrentLocation(field);
                    field.setToken(searchToken);
                    break;
                case 1:
                    field = encounter.getMap().getField(4, 1);
                    searchToken.setCurrentLocation(field);
                    field.setToken(searchToken);
                    break;
                case 2:
                    field = encounter.getMap().getField(12, 5);
                    searchToken.setCurrentLocation(field);
                    field.setToken(searchToken);
                    break;
                default:
                    field = encounter.getMap().getField(0, 7);
                    searchToken.setCurrentLocation(field);
                    field.setToken(searchToken);
                    break;
            }
            encounter.getToken().add(searchToken);
        }
    }

    private static void validateQuest(QuestEncounter encounter) throws QuestValidationException {
        if (encounter.getQuest() != Quest.FIRST_BLOOD) {
            StringBuilder errorMsg = new StringBuilder("Wrong active quest (");
            errorMsg.append(encounter.getQuest().name());
            errorMsg.append("). Expected: ");
            errorMsg.append(Quest.FIRST_BLOOD.name());

            throw new QuestValidationException(errorMsg.toString());
        }
    }

    public static GameMonster getMauler(QuestEncounter encounter) {
        return encounter.getMonsters().stream()
                .filter(monster -> monster.getName().equals(MAULER))
                .findFirst().orElse(null);
    }

}
