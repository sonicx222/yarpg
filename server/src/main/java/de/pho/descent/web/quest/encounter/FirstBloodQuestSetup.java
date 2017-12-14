package de.pho.descent.web.quest.encounter;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.monster.MonsterGroup;
import static de.pho.descent.shared.model.monster.MonsterGroup.*;
import static de.pho.descent.shared.model.monster.MonsterTemplate.*;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.shared.model.token.Token;
import de.pho.descent.shared.model.token.TokenType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author pho
 */
public class FirstBloodQuestSetup {

    private static final QuestTemplate template = QuestTemplate.FIRST_BLOOD_INTRO;
    private static final Map<MonsterGroup, List<GameMonster>> monsterMap = new HashMap<>();
    private static final List<Token> searchTokens = new ArrayList<>();

    public static void setup(QuestEncounter encounter, Campaign campaign) {
        spawnHeroes(encounter, campaign);
        createMonsters(campaign);
        spawnMonsters(encounter);
        createToken(campaign);
        spawnToken(encounter);
    }

    private static void createMonsters(Campaign campaign) {
        int heroCount = campaign.getHeroes().size();

        // goblin archers
        monsterMap.put(GOBLIN_ARCHER, new ArrayList<GameMonster>());
        for (int i = 0; i < GOBLIN_ARCHER.getNormalCount(heroCount); i++) {
            GameMonster gameMonster = new GameMonster(GOBLIN_ARCHER_NORMAL_ACT1);
            gameMonster.setPlayedBy(campaign.getOverlord());
            monsterMap.get(GOBLIN_ARCHER).add(gameMonster);
        }
        if (GOBLIN_ARCHER.hasElite(heroCount)) {
            GameMonster gameMonster = new GameMonster(GOBLIN_ARCHER_ELITE_ACT1);
            gameMonster.setPlayedBy(campaign.getOverlord());
            monsterMap.get(GOBLIN_ARCHER).add(gameMonster);
        }

        // ettins
        monsterMap.put(ETTIN, new ArrayList<GameMonster>());
        for (int i = 0; i < ETTIN.getNormalCount(heroCount); i++) {
            GameMonster gameMonster = new GameMonster(ETTIN_NORMAL_ACT1);
            gameMonster.setPlayedBy(campaign.getOverlord());
            monsterMap.get(ETTIN).add(gameMonster);
        }
        if (ETTIN.hasElite(heroCount)) {
            GameMonster gameMonster = new GameMonster(ETTIN_ELITE_ACT1);
            gameMonster.setPlayedBy(campaign.getOverlord());
            monsterMap.get(ETTIN).add(gameMonster);
        }
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
            hero.setCurrentLocation(heroSpawnFields.get(i));
            i++;
        }
    }

    private static void spawnMonsters(QuestEncounter encounter) {
        // goblins
        List<MapField> goblinSpawnFields = encounter
                .getMap().getMonsterSpawnFields().stream()
                .filter(field -> field.getTileGroup().getName().equals("26A"))
                .collect(Collectors.toList());
        // random spawn order
        Collections.shuffle(goblinSpawnFields);

        for (int i = 0; i < monsterMap.get(GOBLIN_ARCHER).size(); i++) {
            GameMonster goblin = monsterMap.get(GOBLIN_ARCHER).get(i);
            goblin.setCurrentLocation(goblinSpawnFields.get(i));
            encounter.getMonsters().add(goblin);
        }

        // ettins
        List<MapField> ettinSpawnFields = encounter
                .getMap().getMonsterSpawnFields().stream()
                .filter(field -> (field.getxPos() == 5 || field.getxPos() == 7)
                && field.getyPos() == 0)
                .collect(Collectors.toList());
        // random spawn order
        Collections.shuffle(ettinSpawnFields);

        for (int i = 0; i < monsterMap.get(ETTIN).size(); i++) {
            GameMonster ettin = monsterMap.get(ETTIN).get(i);
            ettin.setCurrentLocation(ettinSpawnFields.get(i));
            encounter.getMonsters().add(ettin);
        }
    }

    private static void createToken(Campaign campaign) {
        int heroCount = campaign.getHeroes().size();

        for (int i = 0; i < heroCount; i++) {
            searchTokens.add(new Token(TokenType.SEARCH, true));
        }
    }

    private static void spawnToken(QuestEncounter encounter) {
        encounter.getToken().clear();
        for (int i = 0; i < searchTokens.size(); i++) {
            Token searchToken = searchTokens.get(i);
            switch (i) {
                case 0:
                    searchToken.setCurrentLocation(encounter.getMap().getField(4, 9));
                    break;
                case 1:
                    searchToken.setCurrentLocation(encounter.getMap().getField(4, 1));
                    break;
                case 2:
                    searchToken.setCurrentLocation(encounter.getMap().getField(12, 5));
                    break;
                default:
                    searchToken.setCurrentLocation(encounter.getMap().getField(0, 7));
                    break;
            }
            encounter.getToken().add(searchToken);
        }
    }
}