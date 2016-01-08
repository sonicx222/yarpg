package de.pho.descent.web.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 *
 * @author pho
 */
@Entity
public class MapTileGroup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "mapTileGroups")
    Set<GameMap> gameMaps;

    @OneToMany
    @JoinColumn(name = "group_id")
    Set<MapFieldPos> mapPositions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MapFieldPos> getMapPositions() {
        return mapPositions;
    }

    public void setMapPositions(Set<MapFieldPos> mapPositions) {
        this.mapPositions = mapPositions;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
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
        final MapTileGroup other = (MapTileGroup) obj;

        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "MapTileGroup{" + "id=" + id + ", name=" + name + '}';
    }

}
