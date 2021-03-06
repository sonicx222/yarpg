package de.pho.descent.shared.model.monster;

import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.map.MapField;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

/**
 *
 * @author pho
 */
@Entity
public class GameMonster extends GameUnit implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Enumerated(EnumType.STRING)
    private MonsterTemplate monsterTemplate;
    
    private boolean removed;

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public GameMonster() {
    }

    public GameMonster(MonsterTemplate template) {
        super.setName(template.getGroup().getText());
        super.setMovementPoints(template.getSpeed());
        super.setTotalLife(template.getHealth());
        super.setCurrentLife(template.getHealth());
        super.setActions(2);
        this.monsterTemplate = template;
        this.removed = false;
    }
    
    public MonsterTemplate getMonsterTemplate() {
        return monsterTemplate;
    }

    public void setMonsterTemplate(MonsterTemplate monsterTemplate) {
        this.monsterTemplate = monsterTemplate;
    }
    
   

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
