package de.pho.descent.web.campaign;

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
public class HeroSelectionExceptionMapper implements ExceptionMapper<HeroSelectionException> {

    private static final Logger LOG = Logger.getLogger(HeroSelectionExceptionMapper.class.getName());

    @Override
    public Response toResponse(HeroSelectionException ex) {
        LOG.log(Level.SEVERE, ex.getMessage(), ex);

        return Response.status(Status.CONFLICT)
                .entity(new ErrorMessage(ex.getMessage(), Response.Status.CONFLICT.getStatusCode()))
                .build();
    }

}
