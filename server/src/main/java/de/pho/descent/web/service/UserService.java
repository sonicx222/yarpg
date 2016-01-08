package de.pho.descent.web.service;

import de.pho.descent.web.model.User;
import java.io.Serializable;
import javax.ejb.Stateless;

/**
 *
 * @author pho
 */
@Stateless
public class UserService implements Serializable {

    private static final long serialVersionUID = 1L;

    public User getUserByName(String username) {
        return null;
    }
}
