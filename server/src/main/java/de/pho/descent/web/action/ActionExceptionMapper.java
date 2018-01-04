package de.pho.descent.web.action;

import de.pho.descent.web.campaign.*;
import de.pho.descent.shared.exception.ErrorMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 *
 * @author pho
 */
public class ActionExceptionMapper implements ExceptionMapper<ActionException> {

    private static final Logger LOG = Logger.getLogger(ActionExceptionMapper.class.getName());

    @Override
    public Response toResponse(ActionException ex) {
        LOG.log(Level.SEVERE, ex.getMessage(), ex);

        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorMessage(ex.getMessage(), Response.Status.CONFLICT.getStatusCode()))
                .build();
    }
}
