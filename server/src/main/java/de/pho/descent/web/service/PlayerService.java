package de.pho.descent.web.service;

import de.pho.descent.shared.model.Player;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author pho
 */
@Stateless
public class PlayerService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private transient EntityManager em;

    public Player createPlayer(String username, String password) {
        Player player = null;

        if (getPlayerByUsername(username) == null) {
            player = em.merge(new Player(username, password));
        }

        return player;
    }

    public Player updatePlayer(Player player) {
        return em.merge(player);
    }

    public List<Player> getAllPlayers() {
        return em.createNamedQuery(Player.findAll, Player.class).getResultList();
    }

    public Player getPlayerByUsername(String username) {
        List<Player> players = em.createNamedQuery(Player.findAllByUsername, Player.class).setParameter(Player.paramUsername, username).getResultList();

        return (players.isEmpty()) ? null : players.get(0);
    }

    public Player getPlayerById(long id) {
        Player player = em.createNamedQuery(Player.findById, Player.class).setParameter(Player.paramId, id).getSingleResult();

        return player;
    }
}
