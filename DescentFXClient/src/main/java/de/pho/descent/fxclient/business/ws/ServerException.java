package de.pho.descent.fxclient.business.ws;

/**
 *
 * @author pho
 */
public class ServerException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServerException() {
    }

    public ServerException(String message) {
        super(message);
    }
}
