/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pho.descent.web.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author pho
 */
@Entity
public class MapFieldPos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int xPos;

    private int yPos;

    private int movePoints;

    private boolean isPassable;

    @ManyToOne
    private MapTileGroup group;

    public MapFieldPos() {

    }

    public MapFieldPos(int xPos, int yPos, int movePoints, boolean isPassable, MapTileGroup group) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.movePoints = movePoints;
        this.isPassable = isPassable;
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public int getMovePoints() {
        return movePoints;
    }

    public void setMovePoints(int movePoints) {
        this.movePoints = movePoints;
    }

    public boolean isIsPassable() {
        return isPassable;
    }

    public void setIsPassable(boolean isPassable) {
        this.isPassable = isPassable;
    }

    public MapTileGroup getGroup() {
        return group;
    }

    public void setGroup(MapTileGroup group) {
        this.group = group;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.xPos;
        hash = 23 * hash + this.yPos;
        hash = 23 * hash + this.movePoints;
        hash = 23 * hash + (this.isPassable ? 1 : 0);
        hash = 23 * hash + Objects.hashCode(this.group);
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
        final MapFieldPos other = (MapFieldPos) obj;
        if (this.xPos != other.xPos) {
            return false;
        }
        if (this.yPos != other.yPos) {
            return false;
        }
        if (this.movePoints != other.movePoints) {
            return false;
        }
        if (this.isPassable != other.isPassable) {
            return false;
        }

        return Objects.equals(this.group, other.group);
    }

    @Override
    public String toString() {
        return "MapPosition{" + "id=" + id + ", xPos=" + xPos + ", yPos=" + yPos + ", movePoints=" + movePoints + ", isPassable=" + isPassable + ", group=" + group + '}';
    }

}
