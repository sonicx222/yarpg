package de.pho.descent.shared.model.map;

import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author pho
 */
@Entity
@NamedQueries({
    @NamedQuery(name = GameMap.FIND_MAP_BY_QUEST_AND_PART, query = "select g from GameMap as g "
            + "where g.quest = :" + GameMap.QUEST_PARAM
            + " and g.part = :" + GameMap.QUESTPART_PARAM)
})
public class GameMap implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FIND_MAP_BY_QUEST_AND_PART = "de.pho.descent.shared.model.map.findMapByQuestAndPart";
    public static final String QUEST_PARAM = "paramQuest";
    public static final String QUESTPART_PARAM = "paramQuestPart";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    private Quest quest;

    @Enumerated(EnumType.STRING)
    private QuestPart part;

    private int gridXSize;

    private int gridYSize;

    @OneToMany
    @JoinColumn(name = "map_id")
    @OrderBy("xPos, yPos")
    private List<MapField> mapFields;

    @NotNull
    private String imagePath;

    @Transient
    private Map<Integer, Map<Integer, MapField>> mapLayout;

    public Map<Integer, Map<Integer, MapField>> getMapLayout() {
        if (this.mapLayout == null) {
            this.mapLayout = new HashMap<>();

            for (MapField field : mapFields) {
                if (this.mapLayout.containsKey(field.getxPos())) {
                    this.mapLayout.get(field.getxPos()).put(field.getyPos(), field);
                } else {
                    Map<Integer, MapField> yPosMap = new HashMap<>();
                    yPosMap.put(field.getyPos(), field);
                    this.mapLayout.put(field.getxPos(), yPosMap);
                }
            }
        }

        return this.mapLayout;
    }

    public List<MapField> getHeroSpawnFields() {
        List<MapField> heroSpawnFields = new ArrayList<>();

        for (MapField field : mapFields) {
            if (field.isHeroSpawn()) {
                heroSpawnFields.add(field);
            }
        }

        return heroSpawnFields;
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

    public int getGridXSize() {
        return gridXSize;
    }

    public void setGridXSize(int gridXSize) {
        this.gridXSize = gridXSize;
    }

    public int getGridYSize() {
        return gridYSize;
    }

    public void setGridYSize(int gridYSize) {
        this.gridYSize = gridYSize;
    }

    @XmlTransient
    public List<MapField> getMapFields() {
        return mapFields;
    }

    public void setMapFields(List<MapField> mapFields) {
        this.mapFields = mapFields;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        Character[][] layout = new Character[gridXSize][gridYSize];

        for (int x = 0; x < gridXSize; x++) {
            for (int y = 0; y < gridYSize; y++) {
                layout[x][y] = 'X';
            }
        }

        for (MapField field : mapFields) {
            layout[field.getxPos() - 1][field.getyPos() - 1] = Character.forDigit(field.getMoveCost(), 10);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());

        sb.append(System.lineSeparator()).append('+');
        for (int x = 0; x < gridXSize; x++) {
            sb.append("-+");
        }

        for (int y = 0; y < gridYSize; y++) {
            sb.append(System.lineSeparator()).append('|');
            for (int x = 0; x < gridXSize; x++) {
                sb.append(layout[x][y]).append('|');
            }
            sb.append(System.lineSeparator()).append('+');
            for (int x = 0; x < gridXSize; x++) {
                sb.append("-+");
            }
        }

        return sb.toString();
    }

}
