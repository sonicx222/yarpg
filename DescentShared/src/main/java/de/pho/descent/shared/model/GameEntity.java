package de.pho.descent.shared.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author pho
 */
@Entity
public class GameEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "FIELD_ID", unique = true, nullable = true, insertable = true, updatable = true)
    private MapField currentLocation;

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

    public MapField getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(MapField currentLocation) {
        this.currentLocation = currentLocation;
    }

}
