package de.pho.descent.web.player;

import de.pho.descent.shared.model.Player;
import de.pho.descent.web.player.PlayerAlreadyExistsException;
import de.pho.descent.web.exception.NotFoundException;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author pho
 */
@Stateless
public class PlayerService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private transient EntityManager em;

    public Player createPlayer(String username, String password) throws PlayerAlreadyExistsException {
        Player player = null;
        try {
            player = getPlayerByUsername(username);
            if (player != null) {
                throw new PlayerAlreadyExistsException("Player " + username + " already exists");
            }
        } catch (NoResultException ex) {
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
        TypedQuery<Player> query = em.createNamedQuery(Player.findAllByUsername, Player.class).setParameter(Player.paramUsername, username);
        System.out.println(query);
        
        Player player = query.getSingleResult();
        
        return player;
    }

    public Player getPlayerById(long id) throws NotFoundException {
        Player player = em.find(Player.class, id);

        if (player == null) {
            throw new NotFoundException("Player with Id: " + id + " not found");
        }

        return player;
    }
}
