package de.pho.descent.web.auth;

import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.model.Player;
import de.pho.descent.web.service.PlayerService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 *
 * @author pho
 */
@Stateless
@Path("ejb-player-controller")
public class PlayerController {

    private static final Logger LOG = Logger.getLogger(PlayerController.class.getName());

    @EJB
    private transient PlayerService playerService;

    public Player createPlayer(String username, String password) throws PlayerAlreadyExistsException {
        LOG.log(Level.INFO, "Creating user: {0}", username);

        Player newPlayer = playerService.createPlayer(username, password);

        if (newPlayer == null) {
            throw new PlayerAlreadyExistsException("Player " + username + " already exists!");
        }

        return newPlayer;
    }

    public Player updatePlayer(Player player) {
        LOG.log(Level.INFO, "Updating user: {0}", player.getUsername());

        return playerService.updatePlayer(player);
    }

    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    public Player doLogin(String username, String restMethod, String restURI, String digestHash) throws UserValidationException {
        LOG.log(Level.INFO, "Log in player: {0}", username);

        Player player = playerService.getPlayerByUsername(username);
        if (player == null) {
            throw new UserValidationException("Login failed! User not found");
        }

        // if deactive
        if (player.isDeactive()) {
            throw new UserValidationException("Login failed! User is deactive");
        }

        authenticate(player.getPassword(), restMethod, restURI, digestHash);

        LOG.log(Level.INFO, "User {0} logged in.",
                new Object[]{player.getUsername()});

        return player;
    }
    
    public boolean doAuthenticate(String username, String restMethod, String restURI, String authDigest) throws UserValidationException {
        Player player = playerService.getPlayerByUsername(username);
        
        return authenticate(player.getPassword(), restMethod, restURI, authDigest);
    }

    private boolean authenticate(String pwdHash, String restMethod, String restURI, String authDigest) throws UserValidationException {

        if (!SecurityTools.checkAuthenticationDigest(pwdHash, restMethod, restURI, authDigest)) {
            throw new UserValidationException("Login failed! Check Password");
        }

        return true;
    }
}
