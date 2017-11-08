package de.pho.descent.web.player;

/**
 *
 * @author pho
 */
public class PlayerAlreadyExistsException extends Exception {

    private static final long serialVersionUID = 1L;

    public PlayerAlreadyExistsException() {

    }

    public PlayerAlreadyExistsException(String message) {
        super(message);
    }
}
