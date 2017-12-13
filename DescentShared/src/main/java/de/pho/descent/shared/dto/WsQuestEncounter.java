package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestPart;
import de.pho.descent.shared.model.token.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author pho
 */
public class WsQuestEncounter {

    private long id;

    private Quest quest;

    private QuestPart part;

    private boolean isActive;

    private long activeHeroId;

    private PlaySide winner;

    private long mapId;

    private List<GameMonster> gameMonsters = new ArrayList<>();

    private List<Token> tokens = new ArrayList<>();

    public WsQuestEncounter() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public QuestPart getPart() {
        return part;
    }

    public void setPart(QuestPart part) {
        this.part = part;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public long getActiveHeroId() {
        return activeHeroId;
    }

    public void setActiveHeroId(long activeHeroId) {
        this.activeHeroId = activeHeroId;
    }

    public PlaySide getWinner() {
        return winner;
    }

    public void setWinner(PlaySide winner) {
        this.winner = winner;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public List<GameMonster> getGameMonsters() {
        return gameMonsters;
    }

    public void setGameMonsters(List<GameMonster> gameMonsters) {
        this.gameMonsters = gameMonsters;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Factory-Method to create new WsQuestEncounter DTOs
     *
     * @param questEncounter the copy template for the DTO
     * @return the filled WsQuestEncounter-DTO instance
     */
    public static WsQuestEncounter createInstance(QuestEncounter qe) {
        Objects.requireNonNull(qe);
        WsQuestEncounter wsEncounter = new WsQuestEncounter();

        wsEncounter.setId(qe.getId());
        wsEncounter.setActiveHeroId(qe.getActiveHero().getId());
        wsEncounter.setIsActive(qe.isIsActive());
        wsEncounter.setMapId(qe.getMap().getId());
        wsEncounter.setPart(qe.getPart());
        wsEncounter.setQuest(qe.getQuest());
        wsEncounter.setWinner(qe.getWinner());
        wsEncounter.setGameMonsters(new ArrayList<>(qe.getMonsters()));
        wsEncounter.setTokens(new ArrayList<>(qe.getToken()));

        return wsEncounter;
    }
}
