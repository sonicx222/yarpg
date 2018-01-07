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
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author pho
 */
public class WsQuestEncounter {

    private long id;

    private Quest quest;

    private QuestPart part;

    private boolean isActive;
    
        private PlaySide currentTurn;

    private PlaySide winner;

    private int round;

    private long mapId;

    private List<GameMonster> gameMonsters = new ArrayList<>();

    private List<Token> tokens = new ArrayList<>();

    public WsQuestEncounter() {
    }

    @XmlTransient
    public GameMonster getActiveMonster() {
        return gameMonsters.stream()
                .filter(monster -> monster.isActive()).findAny().orElse(null);
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

    public PlaySide getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(PlaySide currentTurn) {
        this.currentTurn = currentTurn;
    }

    public PlaySide getWinner() {
        return winner;
    }

    public void setWinner(PlaySide winner) {
        this.winner = winner;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
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
        wsEncounter.setIsActive(qe.isActive());
        wsEncounter.setMapId(qe.getMap().getId());
        wsEncounter.setPart(qe.getPart());
        wsEncounter.setQuest(qe.getQuest());
        wsEncounter.setCurrentTurn(qe.getCurrentTurn());
        wsEncounter.setWinner(qe.getWinner());
        wsEncounter.setRound(qe.getRound());
        wsEncounter.setGameMonsters(new ArrayList<>(qe.getMonsters()));
        wsEncounter.setTokens(new ArrayList<>(qe.getToken()));

        return wsEncounter;
    }
}
