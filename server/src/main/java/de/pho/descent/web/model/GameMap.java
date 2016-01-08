package de.pho.descent.web.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pho
 */
@Entity
@XmlRootElement
public class GameMap implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int gridXSize;

    private int gridYSize;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "map_groups", joinColumns = { 
			@JoinColumn(name = "map_id", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "group_id", 
					nullable = false, updatable = false) })
    private Set<MapTileGroup> mapTileGroups;

    @Lob
    @NotNull
    private byte[] image;

    @Transient
    private Map<Integer, Map<Integer, MapFieldPos>> mapLayout;

    public Map<Integer, Map<Integer, MapFieldPos>> getMapLayout() {
        if (this.mapLayout == null) {
            this.mapLayout = new HashMap<>();

            for (MapTileGroup group : mapTileGroups) {
                for (MapFieldPos loc : group.getMapPositions()) {
                    if (this.mapLayout.containsKey(loc.getXPos())) {
                        this.mapLayout.get(loc.getXPos()).put(loc.getYPos(), loc);
                    } else {
                        Map<Integer, MapFieldPos> yPosMap = new HashMap<>();
                        yPosMap.put(loc.getYPos(), loc);
                        this.mapLayout.put(loc.getXPos(), yPosMap);
                    }
                }
            }
        }

        return this.mapLayout;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GameMap)) {
            return false;
        }
        GameMap other = (GameMap) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "de.pho.descent.web.model.Map[ id=" + id + " ]";
    }

}
