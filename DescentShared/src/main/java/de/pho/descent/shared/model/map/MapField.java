package de.pho.descent.shared.model.map;

import de.pho.descent.shared.model.token.Token;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 *
 * @author pho
 */
@Entity
@NamedQueries({
    @NamedQuery(name = MapField.FIND_SPAWNS_BY_MAPID, query = "select mf from MapField as mf "
            + "where mf.heroSpawn = true and mf.map.id = :" + MapField.MAPID_PARAM)
    ,
    @NamedQuery(name = MapField.FIND_SPAWNS_BY_GROUPID, query = "select mf from MapField as mf "
            + "where mf.heroSpawn = true and mf.tileGroup.id = :" + MapField.GROUPID_PARAM)
})
public class MapField implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final String FIND_SPAWNS_BY_MAPID = "de.pho.descent.shared.model.MapField.findSpawnsByMapId";
    public static final String FIND_SPAWNS_BY_GROUPID = "de.pho.descent.shared.model.MapField.findSpawnsByGroupId";
    public static final String MAPID_PARAM = "paramMapId";
    public static final String GROUPID_PARAM = "paramGroupId";

    @Id
    private Long id;

    @ManyToOne
    private MapTileGroup tileGroup;

    @ManyToOne
    private GameMap map;

    private int xPos;

    private int yPos;

    private int moveCost;

    private boolean passable;

    private boolean heroSpawn;

    private boolean monsterSpawn;

    @OneToOne(optional = true)
    private Token objectiveToken;

    @OneToOne(optional = true)
    private Token searchToken;

    @OneToOne(optional = true)
    private Token villagerToken;

    public MapField() {

    }

    public MapField(MapTileGroup tileGroup, GameMap map, int xPos, int yPos, int moveCost, boolean passable, boolean heroSpawn, boolean monsterSpawn, Token objectiveToken, Token searchToken, Token villagerToken) {
        this.tileGroup = tileGroup;
        this.map = map;
        this.xPos = xPos;
        this.yPos = yPos;
        this.moveCost = moveCost;
        this.passable = passable;
        this.heroSpawn = heroSpawn;
        this.monsterSpawn = monsterSpawn;
        this.objectiveToken = objectiveToken;
        this.searchToken = searchToken;
        this.villagerToken = villagerToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MapTileGroup getTileGroup() {
        return tileGroup;
    }

    public void setTileGroup(MapTileGroup tileGroup) {
        this.tileGroup = tileGroup;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public void setMoveCost(int moveCost) {
        this.moveCost = moveCost;
    }

    public boolean isPassable() {
        return passable;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    public boolean isHeroSpawn() {
        return heroSpawn;
    }

    public void setHeroSpawn(boolean heroSpawn) {
        this.heroSpawn = heroSpawn;
    }

    public boolean isMonsterSpawn() {
        return monsterSpawn;
    }

    public void setMonsterSpawn(boolean monsterSpawn) {
        this.monsterSpawn = monsterSpawn;
    }

}
