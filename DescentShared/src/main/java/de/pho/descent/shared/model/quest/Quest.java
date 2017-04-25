package de.pho.descent.shared.model.quest;

import static de.pho.descent.shared.model.quest.QuestType.*;

/**
 *
 * @author pho
 */
public enum Quest {

    FIRST_BLOOD(INTRO),
    A_FAT_GOBLIN(ACT_ONE),
    CASTLE_DAERION(ACT_ONE),
    THE_CARDINALS_PLIGHT(ACT_ONE),
    THE_MASQUERADE_BALL(ACT_ONE),
    DEATH_ON_THE_WING(ACT_ONE),
    THE_SHADOW_VAULT(INTERLUDE),
    THE_OVERLORD_REVEALED(INTERLUDE),
    THE_MONSTERS_HOARD(ACT_TWO),
    THE_FROZEN_SPIRE(ACT_TWO),
    THE_DAWNBLADE(ACT_TWO),
    THE_DESECRATED_TOMB(ACT_TWO),
    ENDURING_THE_ELEMENTS(ACT_TWO),
    THE_RITUAL_OF_SHADOWS(ACT_TWO),
    BLOOD_OF_HEROES(ACT_TWO),
    THE_TWIN_IDOLS(ACT_TWO),
    THE_WYRM_TURNS(ACT_TWO),
    THE_WYRM_RISES(ACT_TWO),
    GRYVORN_UNLEASHED(FINALE),
    THE_MAN_WHO_WOULD_BE_KING(FINALE);

    private final QuestType type;

    private Quest followQuestOverlord;

    private Quest followQuestHeroes;

    private Quest(QuestType type) {
        this.type = type;
    }

    // followup quests based on winning side
    static {
        A_FAT_GOBLIN.followQuestHeroes = THE_MONSTERS_HOARD;
        A_FAT_GOBLIN.followQuestOverlord = THE_FROZEN_SPIRE;

        CASTLE_DAERION.followQuestHeroes = THE_DAWNBLADE;
        CASTLE_DAERION.followQuestOverlord = THE_DESECRATED_TOMB;

        THE_CARDINALS_PLIGHT.followQuestHeroes = ENDURING_THE_ELEMENTS;
        THE_CARDINALS_PLIGHT.followQuestOverlord = THE_RITUAL_OF_SHADOWS;

        THE_MASQUERADE_BALL.followQuestHeroes = BLOOD_OF_HEROES;
        THE_MASQUERADE_BALL.followQuestOverlord = THE_TWIN_IDOLS;

        DEATH_ON_THE_WING.followQuestHeroes = THE_WYRM_TURNS;
        DEATH_ON_THE_WING.followQuestOverlord = THE_WYRM_RISES;
    }

    public QuestType getType() {
        return type;
    }
    
    public static Quest getIntroQuest() {
        return Quest.FIRST_BLOOD;
    }

    public Quest getFollowQuestOverlord() {
        return followQuestOverlord;
    }

    public Quest getFollowQuestHeroes() {
        return followQuestHeroes;
    }

}
