package de.pho.descent.shared.model.monster;

import de.pho.descent.shared.model.GameEntity;
import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author pho
 */
@Entity
public class GameMonster extends GameEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (super.getId() != null ? super.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GameMonster)) {
            return false;
        }
        GameMonster other = (GameMonster) object;
        if ((super.getId() == null && other.getId() != null) || (super.getId() != null && !super.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "de.pho.descent.shared.model.Monster[ id=" + super.getId() + " ]";
    }

}
