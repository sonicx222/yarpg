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
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable th) {
        LOG.log(Level.SEVERE, th.getMessage(), th);

        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorMessage(th.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                .build();
    }

}
