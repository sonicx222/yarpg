package de.pho.descent.web.player;

import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.model.Player;
import de.pho.descent.web.auth.UserValidationException;
import de.pho.descent.web.exception.NotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 *
 * @author pho
 */
@Stateless
@Path("ejb-player-controller")
public class PlayerController {

    private static final Logger LOG = Logger.getLogger(PlayerController.class.getName());

    @Inject
    private transient PlayerService playerService;

    public Player createPlayer(String username, String password) throws PlayerAlreadyExistsException {
        LOG.log(Level.INFO, "Creating user: {0}", username);

        Player newPlayer = playerService.createPlayer(username, password);

        if (newPlayer == null) {
            throw new PlayerAlreadyExistsException("Player " + username + " already exists");
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

    public Player getPlayer(long id) throws NotFoundException {
        return playerService.getPlayerById(id);
    }

    public Player getPlayerByName(String username) throws NotFoundException {
        return playerService.getPlayerByUsername(username);
    }

    public Player getPlayerByToken(String authToken) throws UserValidationException, NotFoundException {
        String[] authData = SecurityTools.extractDataFromAuthenticationToken(authToken);
        String decodedUser = authData[0];

        Player player = playerService.getPlayerByUsername(decodedUser);

        // if deactive
        if (player.isDeactive()) {
            throw new UserValidationException("User is deactive");
        }

        return player;
    }

    public Player doAuthenticate(String restMethod, String restURI, String authToken) throws UserValidationException, NotFoundException {
        if (authToken == null) {
            throw new UserValidationException("No auth token");
        }

        String[] authData = SecurityTools.extractDataFromAuthenticationToken(authToken);
        String decodedUser = authData[0];
        String digestHash = authData[1];

        LOG.log(Level.INFO, "Authenticate player: {0}", decodedUser);
        Player player = playerService.getPlayerByUsername(decodedUser);

        // if deactive
        if (player.isDeactive()) {
            throw new UserValidationException("Login failed! User is deactive");
        }

        authenticate(player.getPassword(), restMethod, restURI, digestHash);

        return player;
    }

    private boolean authenticate(String pwdHash, String restMethod, String restURI, String authDigest) throws UserValidationException {

        if (!SecurityTools.checkAuthenticationDigest(pwdHash, restMethod, restURI, authDigest)) {
            throw new UserValidationException("Login failed! Check Password");
        }

        return true;
    }
}
