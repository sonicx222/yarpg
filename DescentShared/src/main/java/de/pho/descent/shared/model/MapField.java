package de.pho.descent.shared.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author pho
 */
@Entity
public class MapField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private int xPos;

    private int yPos;

    private int moveCost;

    private boolean isPassable;

    public MapField() {

    }

    public MapField(int xPos, int yPos, int movePoints, boolean isPassable) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.moveCost = movePoints;
        this.isPassable = isPassable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isIsPassable() {
        return isPassable;
    }

    public void setIsPassable(boolean isPassable) {
        this.isPassable = isPassable;
    }

}
