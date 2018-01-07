package de.pho.descent.shared.model.quest;

import de.pho.descent.shared.model.monster.MonsterGroup;
import de.pho.descent.shared.model.monster.MonsterTrait;
import java.util.Objects;

/**
 *
 * @author pho
 */
public enum QuestTemplate {
    FIRST_BLOOD_INTRO(Quest.FIRST_BLOOD, QuestPart.FIRST, MonsterGroup.GOBLIN_ARCHER, MonsterGroup.ETTIN, 0, MonsterTrait.WILDERNESS, MonsterTrait.MOUNTAIN,
            "As you are travelling toward Arhynn, you come across the still-smoldering remains of a travelling caravan. One of the caravan guards, wearing the livery of Baron Greigory, lies wounded nearby.\"An ambush,\" he gasps. \"That ettin is sending his minions straight into Arhynn through the secret path! How could he have known?\""
            + "\nAhead, you see the lumbering, two-headed form of an ettin. \"He said his name was Mauler. You must stop him!\""
            + "\nMauler's goblin minions are taking a secret route to attack Arhynn, Baron Greigory's seat. If too many of his goblins escape off the far side of the trail, Mauler will have triumphed over you. Your goal is to kill him before that happens."
    ),
    A_FAT_GOBLIN_FIRST(Quest.A_FAT_GOBLIN, QuestPart.FIRST, MonsterGroup.GOBLIN_ARCHER, null, 1, MonsterTrait.WILDERNESS, MonsterTrait.CIVILIZED, null),
    A_FAT_GOBLIN_SECOND(Quest.A_FAT_GOBLIN, QuestPart.SECOND, MonsterGroup.GOBLIN_ARCHER, MonsterGroup.CAVE_SPIDER, 1, MonsterTrait.CAVE, MonsterTrait.BUILDING, null);

    private final Quest quest;

    private final QuestPart questPart;

    private final MonsterGroup requiredGroup1;

    private final MonsterGroup requiredGroup2;

    private final int openGroupCount;

    private final MonsterTrait monsterTrait1;

    private final MonsterTrait monsterTrait2;

    private final String prolog;

    private QuestTemplate(Quest quest, QuestPart questPart, MonsterGroup requiredGroup1, MonsterGroup requiredGroup2, int openGroupCount, MonsterTrait monsterTrait1, MonsterTrait monsterTrait2, String prolog) {
        this.quest = quest;
        this.questPart = questPart;
        this.requiredGroup1 = requiredGroup1;
        this.requiredGroup2 = requiredGroup2;
        this.openGroupCount = openGroupCount;
        this.monsterTrait1 = monsterTrait1;
        this.monsterTrait2 = monsterTrait2;
        this.prolog = prolog;
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

    public String getProlog() {
        return prolog;
    }

    
}
