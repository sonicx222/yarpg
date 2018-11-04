package de.pho.descent.shared.model.quest;

import de.pho.descent.shared.model.monster.MonsterGroup;
import de.pho.descent.shared.model.monster.MonsterTrait;
import java.util.Objects;

/**
 *
 * @author pho
 */
public enum QuestTemplate {
    FIRST_BLOOD_INTRO(Quest.FIRST_BLOOD, QuestPart.FIRST, 13, 12, MonsterGroup.GOBLIN_ARCHER, MonsterGroup.ETTIN, 0, MonsterTrait.WILDERNESS, MonsterTrait.MOUNTAIN),
    A_FAT_GOBLIN_FIRST(Quest.A_FAT_GOBLIN, QuestPart.FIRST, 14, 11, MonsterGroup.GOBLIN_ARCHER, null, 1, MonsterTrait.WILDERNESS, MonsterTrait.CIVILIZED),
    A_FAT_GOBLIN_SECOND(Quest.A_FAT_GOBLIN, QuestPart.SECOND, 16, 13, MonsterGroup.GOBLIN_ARCHER, MonsterGroup.CAVE_SPIDER, 1, MonsterTrait.CAVE, MonsterTrait.BUILDING);

    private final Quest quest;

    private final QuestPart questPart;

    private final int gridXSize;

    private final int gridYSize;

    private final MonsterGroup requiredGroup1;

    private final MonsterGroup requiredGroup2;

    private final int openGroupCount;

    private final MonsterTrait monsterTrait1;

    private final MonsterTrait monsterTrait2;

    private QuestTemplate(Quest quest, QuestPart questPart, int gridXSize, int gridYSize, MonsterGroup requiredGroup1, MonsterGroup requiredGroup2, int openGroupCount, MonsterTrait monsterTrait1, MonsterTrait monsterTrait2) {
        this.quest = quest;
        this.questPart = questPart;
        this.gridXSize = gridXSize;
        this.gridYSize = gridYSize;
        this.requiredGroup1 = requiredGroup1;
        this.requiredGroup2 = requiredGroup2;
        this.openGroupCount = openGroupCount;
        this.monsterTrait1 = monsterTrait1;
        this.monsterTrait2 = monsterTrait2;
    }

    public static QuestTemplate getTemplate(Quest quest, QuestPart part) {
        QuestTemplate questTemplate = null;
        for (QuestTemplate template : QuestTemplate.values()) {
            if (Objects.equals(template.getQuest(), quest)
                    && Objects.equals(template.getQuestPart(), part)) {
                questTemplate = template;
                break;
            }
        }

        return questTemplate;
    }

    public Quest getQuest() {
        return quest;
    }

    public QuestPart getQuestPart() {
        return questPart;
    }

    public int getGridXSize() {
        return gridXSize;
    }

    public int getGridYSize() {
        return gridYSize;
    }

    public MonsterGroup getRequiredGroup1() {
        return requiredGroup1;
    }

    public MonsterGroup getRequiredGroup2() {
        return requiredGroup2;
    }

    public int getOpenGroupCount() {
        return openGroupCount;
    }

    public MonsterTrait getMonsterTrait1() {
        return monsterTrait1;
    }

    public MonsterTrait getMonsterTrait2() {
        return monsterTrait2;
    }

}
