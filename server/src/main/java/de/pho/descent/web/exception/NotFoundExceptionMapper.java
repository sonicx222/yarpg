package de.pho.descent.web.exception;

import de.pho.descent.shared.exception.ErrorMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 *
 * @author pho
 */
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    private static final Logger LOG = Logger.getLogger(NotFoundExceptionMapper.class.getName());

    @Override
    public Response toResponse(NotFoundException ex) {
        LOG.log(Level.SEVERE, ex.getMessage(), ex);

        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorMessage(ex.getMessage(), Response.Status.CONFLICT.getStatusCode()))
                .build();
    }
}
