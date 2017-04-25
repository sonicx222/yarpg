package de.pho.descent.web.auth;

import de.pho.descent.shared.model.Player;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;

/**
 *
 * @author pho
 */
public class UserValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public UserValidationException() {
    }

    private Set<ConstraintViolation<Player>> violations = new HashSet<>();

    public UserValidationException(Set<ConstraintViolation<Player>> violations) {
        this.violations = violations;
    }

    public UserValidationException(String message) {
        super(message);
    }

    public Set<ConstraintViolation<Player>> getViolations() {
        return violations;
    }

    public void setViolations(Set<ConstraintViolation<Player>> violations) {
        this.violations = violations;
    }
}
