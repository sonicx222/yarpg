package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.Player;

/**
 *
 * @author pho
 */
public class WsPlayer {

    private Long id;

    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Factory-Method to create new WsPlayer DTOs
     *
     * @param player the copy template for the DTO
     * @return the filled WsPlayer-DTO instance
     */
    public static WsPlayer createInstance(Player player) {
        WsPlayer wsPlayer = new WsPlayer();

        wsPlayer.setId(player.getId());
        wsPlayer.setUsername(player.getUsername());

        return wsPlayer;
    }

    @Override
    public String toString() {
        return "WsPlayer{" + "id=" + id + ", username=" + username + '}';
    }

}
