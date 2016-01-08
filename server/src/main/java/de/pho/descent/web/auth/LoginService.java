package de.pho.descent.web.auth;

import de.pho.descent.web.model.User;
import de.pho.descent.web.service.UserService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
@Stateless
public class LoginService {

    private static final Logger LOG = Logger.getLogger(LoginService.class.getName());

    @Inject
    private transient UserService userService;

    public User doLogin(String username, String password) throws UserNotFoundException, UserValidationException {
        LOG.log(Level.FINE, "check for username: {0}", username);

        User user = userService.getUserByName(username);
        if (user == null) {
            throw new UserNotFoundException();
        }

        checkUserDataForLogin(user, password);

        LOG.log(Level.INFO, "User {0} logged in.",
                new Object[]{user.getUsername()});

        return user;
    }

    private boolean checkUserDataForLogin(User user, String passwort) throws UserValidationException {

        if (!SecurityTools.checkPassword(passwort, user.getPassword())) {
            throw new UserValidationException("Login failed! Check Password");
        }

        // if deactive
        if (user.isIsDeactive()) {
            throw new UserValidationException("Login failed! User is deactive");
        }

        return true;
    }
}
