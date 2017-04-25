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
public class UserValidationExceptionMapper implements ExceptionMapper<UserValidationException> {

    private static final Logger LOG = Logger.getLogger(UserValidationExceptionMapper.class.getName());

    @Override
    public Response toResponse(UserValidationException ex) {
        LOG.log(Level.SEVERE, ex.getMessage(), ex);

        return Response.status(Status.UNAUTHORIZED)
                .entity(new ErrorMessage(ex.getMessage(), Status.UNAUTHORIZED.getStatusCode()))
                .build();
    }

}
