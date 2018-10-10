package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.action.ActionType;
import de.pho.descent.shared.model.map.MapField;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pho
 */
public class WsAction {

    private long campaignId;

    private long questEncounterId;

    private boolean heroAction;

    private ActionType type;

    private long sourceUnitId;

    private long targetUnitId;

    private List<MapField> sourceFields = new ArrayList<>();

    private List<MapField> targetFields = new ArrayList<>();

    public WsAction() {
    }

    public WsAction(long campaignId, long questEncounterId, boolean heroAction, ActionType type, long sourceUnitId, long targetUnitId) {
        this.campaignId = campaignId;
        this.questEncounterId = questEncounterId;
        this.heroAction = heroAction;
        this.type = type;
        this.sourceUnitId = sourceUnitId;
        this.targetUnitId = targetUnitId;
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

    public long getSourceUnitId() {
        return sourceUnitId;
    }

    public void setSourceUnitId(long sourceUnitId) {
        this.sourceUnitId = sourceUnitId;
    }

    public long getTargetUnitId() {
        return targetUnitId;
    }

    public void setTargetUnitId(long targetUnitId) {
        this.targetUnitId = targetUnitId;
    }

    public List<MapField> getSourceFields() {
        return sourceFields;
    }

    public void setSourceFields(List<MapField> sourceFields) {
        this.sourceFields = sourceFields;
    }

    public List<MapField> getTargetFields() {
        return targetFields;
    }

    public void setTargetFields(List<MapField> targetFields) {
        this.targetFields = targetFields;
    }

}
