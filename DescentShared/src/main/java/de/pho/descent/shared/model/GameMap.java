package de.pho.descent.shared.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author pho
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class GameMap implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int gridXSize;

    private int gridYSize;

    @OneToMany
    @JoinColumn(name = "map_id")
    @OrderBy("xPos, yPos")
    private List<MapField> mapFields;

    @Lob
    @NotNull
    private byte[] image;

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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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
