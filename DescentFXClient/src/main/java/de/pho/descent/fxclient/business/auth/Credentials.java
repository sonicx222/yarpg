package de.pho.descent.fxclient.business.auth;

import de.pho.descent.shared.model.Player;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class Credentials {

    private Player player;

    public Credentials() {
    }

    public Credentials(Player player) {
        this.player = player;
    }

    @PostConstruct
    public void init() {
    }

    public boolean isValid() {
        return player != null
                && player.getUsername() != null
                && !player.getUsername().isEmpty()
                && player.getPassword() != null
                && !player.getPassword().isEmpty();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getUsername() {
        return this.player.getUsername();
    }

    public String getPassword() {
        return this.player.getPassword();
    }

}
