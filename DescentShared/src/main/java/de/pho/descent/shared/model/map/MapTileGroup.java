package de.pho.descent.shared.model.map;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    
    @OneToMany(mappedBy="tileGroup")
    List<MapField> mapFields;

    public MapTileGroup() {
    }

    public MapTileGroup(String name, List<MapField> mapFields) {
        this.name = name;
        this.mapFields = mapFields;
    }
    
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

    public List<MapField> getMapFields() {
        return mapFields;
    }

    public void setMapFields(List<MapField> mapFields) {
        this.mapFields = mapFields;
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
