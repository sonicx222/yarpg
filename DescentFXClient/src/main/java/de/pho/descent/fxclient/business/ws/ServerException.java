package de.pho.descent.fxclient.business.ws;

import javax.ws.rs.core.Response;

/**
 *
 * @author pho
 */
public class ServerException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private Response errorResponse;

    public ServerException() {
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(Response errorResponse) {
        this.errorResponse = errorResponse;
    }

    public Response getErrorResponse() {
        return errorResponse;
    }
}
