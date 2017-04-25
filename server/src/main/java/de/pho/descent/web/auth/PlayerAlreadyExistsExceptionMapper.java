package de.pho.descent.web.auth;

import de.pho.descent.shared.exception.ErrorMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author pho
 */
@Provider
public class PlayerAlreadyExistsExceptionMapper implements ExceptionMapper<PlayerAlreadyExistsException> {

    private static final Logger LOG = Logger.getLogger(PlayerAlreadyExistsExceptionMapper.class.getName());

    @Override
    public Response toResponse(PlayerAlreadyExistsException ex) {
        LOG.log(Level.SEVERE, ex.getMessage(), ex);
        
        return Response.status(Status.CONFLICT)
                .entity(new ErrorMessage(ex.getMessage(), Response.Status.CONFLICT.getStatusCode()))
                .build();
    }

}
