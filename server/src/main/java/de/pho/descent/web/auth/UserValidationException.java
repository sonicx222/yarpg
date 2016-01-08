package de.pho.descent.web.auth;

import de.pho.descent.web.model.User;
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

    private Set<ConstraintViolation<User>> violations = new HashSet<>();

    public UserValidationException(Set<ConstraintViolation<User>> violations) {
        this.violations = violations;
    }

    public UserValidationException(String message) {
        super(message);
    }

    public Set<ConstraintViolation<User>> getViolations() {
        return violations;
    }

    public void setViolations(Set<ConstraintViolation<User>> violations) {
        this.violations = violations;
    }
}
