package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.map.MapField;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Data Transfer Object between Rest-WS and Persistence Layer, mainly for Base64
 * encoding of the Image byte[]
 *
 * @author pho
 */
public class WsGameMap {

    private long id;

    private String imagePath;

    private int gridXSize;

    private int gridYSize;

    private List<MapField> mapFields;

    public static WsGameMap createInstance(GameMap map) {
        WsGameMap wsMap = new WsGameMap();

        wsMap.setId(map.getId());
        wsMap.setGridXSize(map.getGridXSize());
        wsMap.setGridYSize(map.getGridYSize());
        wsMap.setMapFields(map.getMapFields());
        wsMap.setImagePath(map.getImagePath());

        return wsMap;
    }

    @XmlTransient
    public MapField getField(int x, int y) {
        return mapFields.stream()
                .filter(field -> (field.getxPos() == x && field.getyPos() == y))
                .findAny()
                .orElse(null);
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

    public List<MapField> getMapFields() {
        return mapFields;
    }

    public void setMapFields(List<MapField> mapFields) {
        this.mapFields = mapFields;
    }
}
