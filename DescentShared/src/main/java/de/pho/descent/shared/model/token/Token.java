package de.pho.descent.shared.model.token;

import de.pho.descent.shared.model.GameEntity;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author pho
 */
@Entity
public class Token extends GameEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    private boolean active;

    public Token() {
    }

    public Token(TokenType type, boolean active) {
        this.type = type;
        this.active = active;
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
