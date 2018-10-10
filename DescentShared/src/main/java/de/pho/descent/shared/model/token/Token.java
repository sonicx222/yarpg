package de.pho.descent.shared.model.token;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.pho.descent.shared.model.GameEntity;
import de.pho.descent.shared.model.map.MapField;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 *
 * @author pho
 */
@Entity
public class Token extends GameEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @OneToOne(mappedBy = "token")
    @JsonManagedReference(value="token-location")
    private MapField currentLocation;
    
    @Enumerated(EnumType.STRING)
    private TokenType type;

    private boolean active;

    public Token() {
    }

    public Token(TokenType type, boolean active) {
        this.type = type;
        this.active = active;
    }

    public MapField getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(MapField currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
