package de.pho.descent.web.dto;

import de.pho.descent.shared.model.GameMap;
import de.pho.descent.shared.model.MapField;
import java.util.List;
import org.apache.commons.codec.binary.Base64;

/**
 * Data Transfer Object between Rest-WS and Persistence Layer, mainly for Base64
 * encoding of the Image byte[]
 *
 * @author pho
 */
public class WsGameMap extends WsBaseEntity {

    /**
     * Base64 encoded
     */
    private String imageContent;

    private int gridXSize;

    private int gridYSize;

    private List<MapField> mapFields;

    public static WsGameMap createInstance(GameMap map) {
        WsGameMap wsMap = new WsGameMap();

        wsMap.setId(map.getId());
        wsMap.setGridXSize(map.getGridXSize());
        wsMap.setGridYSize(map.getGridYSize());
        wsMap.setMapFields(map.getMapFields());
        wsMap.setImageContent(Base64.encodeBase64String(map.getImage()));

        return wsMap;
    }

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
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
