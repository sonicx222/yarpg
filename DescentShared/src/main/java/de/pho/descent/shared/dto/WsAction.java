package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.action.ActionType;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.monster.GameMonster;

/**
 *
 * @author pho
 */
public class WsAction {

    private long campaignId;

    private long questEncounterId;
    
    private boolean heroAction;

    private ActionType type;

    private GameHero sourceHero;
    
    private GameHero targetHero;

    private GameMonster sourceMonster;
    
    private GameMonster targetMonster;

    private MapField sourceField;

    private MapField targetField;

    public WsAction() {
    }

    public WsAction(long campaignId, long questEncounterId, boolean heroAction, ActionType type, GameHero sourceHero, GameHero targetHero, GameMonster sourceMonster, GameMonster targetMonster, MapField sourceField, MapField targetField) {
        this.campaignId = campaignId;
        this.questEncounterId = questEncounterId;
        this.heroAction = heroAction;
        this.type = type;
        this.sourceHero = sourceHero;
        this.targetHero = targetHero;
        this.sourceMonster = sourceMonster;
        this.targetMonster = targetMonster;
        this.sourceField = sourceField;
        this.targetField = targetField;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public long getQuestEncounterId() {
        return questEncounterId;
    }

    public void setQuestEncounterId(long questEncounterId) {
        this.questEncounterId = questEncounterId;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public boolean isHeroAction() {
        return heroAction;
    }

    public void setHeroAction(boolean heroAction) {
        this.heroAction = heroAction;
    }

    public GameHero getSourceHero() {
        return sourceHero;
    }

    public void setSourceHero(GameHero sourceHero) {
        this.sourceHero = sourceHero;
    }

    public GameHero getTargetHero() {
        return targetHero;
    }

    public void setTargetHero(GameHero targetHero) {
        this.targetHero = targetHero;
    }

    public GameMonster getSourceMonster() {
        return sourceMonster;
    }

    public void setSourceMonster(GameMonster sourceMonster) {
        this.sourceMonster = sourceMonster;
    }

    public GameMonster getTargetMonster() {
        return targetMonster;
    }

    public void setTargetMonster(GameMonster targetMonster) {
        this.targetMonster = targetMonster;
    }



    public MapField getSourceField() {
        return sourceField;
    }

    public void setSourceField(MapField sourceField) {
        this.sourceField = sourceField;
    }

    public MapField getTargetField() {
        return targetField;
    }

    public void setTargetField(MapField targetField) {
        this.targetField = targetField;
    }

}
