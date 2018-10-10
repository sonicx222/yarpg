package de.pho.descent.shared.model.map;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.token.Token;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

    @ManyToOne(optional = true)
    @JoinColumn(name = "gameunit_id", nullable = true)
    @JsonBackReference(value="unit-location")
    private GameUnit gameUnit;

//    @ManyToOne
//    @JoinColumn(name = "gamemonster_id")
//    private GameMonster gameMonster;
    
    
    @OneToOne(optional = true)
    @JoinColumn(name = "token_id", nullable = true)
    @JsonBackReference(value="token-location")
    private Token token;

    private int xPos;

    private int yPos;

    private int moveCost;

    private boolean passable;

    private boolean heroSpawn;

    private boolean monsterSpawn;

    public MapField() {
    }

    public MapField(MapTileGroup tileGroup, GameUnit gameUnit, Token token, int xPos, int yPos, int moveCost, boolean passable, boolean heroSpawn, boolean monsterSpawn) {
        this.tileGroup = tileGroup;
        this.gameUnit = gameUnit;
        this.token = token;
        this.xPos = xPos;
        this.yPos = yPos;
        this.moveCost = moveCost;
        this.passable = passable;
        this.heroSpawn = heroSpawn;
        this.monsterSpawn = monsterSpawn;
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

//    public GameHero getGameHero() {
//        return gameHero;
//    }
//
//    public void setGameHero(GameHero gameHero) {
//        this.gameHero = gameHero;
//    }
//
//    public GameMonster getGameMonster() {
//        return gameMonster;
//    }
//
//    public void setGameMonster(GameMonster gameMonster) {
//        this.gameMonster = gameMonster;
//    }
    public GameUnit getGameUnit() {
        return gameUnit;
    }

    public void setGameUnit(GameUnit gameUnit) {
        this.gameUnit = gameUnit;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.tileGroup);
        hash = 97 * hash + this.xPos;
        hash = 97 * hash + this.yPos;
        hash = 97 * hash + this.moveCost;
        hash = 97 * hash + (this.passable ? 1 : 0);
        hash = 97 * hash + (this.heroSpawn ? 1 : 0);
        hash = 97 * hash + (this.monsterSpawn ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MapField other = (MapField) obj;
        if (this.xPos != other.xPos) {
            return false;
        }
        if (this.yPos != other.yPos) {
            return false;
        }
        if (this.moveCost != other.moveCost) {
            return false;
        }
        if (this.passable != other.passable) {
            return false;
        }
        if (this.heroSpawn != other.heroSpawn) {
            return false;
        }
        if (this.monsterSpawn != other.monsterSpawn) {
            return false;
        }
        if (!Objects.equals(this.tileGroup, other.tileGroup)) {
            return false;
        }
        return true;
    }



}
