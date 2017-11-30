package de.pho.descent.shared.model.token;

import de.pho.descent.shared.model.GameEntity;
import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author pho
 */
@Entity
public class Token extends GameEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private TokenType type;
    
    private String imagePath;

    public Token() {
    }

    public Token(TokenType type, String imagePath) {
        this.type = type;
        this.imagePath = imagePath;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    
}
