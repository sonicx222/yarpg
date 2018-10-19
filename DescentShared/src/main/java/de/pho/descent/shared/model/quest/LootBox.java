package de.pho.descent.shared.model.quest;

import de.pho.descent.shared.model.PlaySide;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains quest reward for each playside
 *
 * @author pho
 */
public class LootBox {

    private Map<PlaySide, QuestReward> rewards = new HashMap<>();

    public QuestReward getRewardBySide(PlaySide playside) {
        return rewards.get(playside);
    }

    public QuestReward setRewardBySide(PlaySide playside, QuestReward reward) {
        return rewards.put(playside, reward);
    }

    public Map<PlaySide, QuestReward> getRewards() {
        return rewards;
    }

    public void setRewards(Map<PlaySide, QuestReward> rewards) {
        this.rewards = rewards;
    }
}
