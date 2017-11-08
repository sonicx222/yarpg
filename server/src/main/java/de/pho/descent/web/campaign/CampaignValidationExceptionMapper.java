package de.pho.descent.web.campaign;

import de.pho.descent.shared.exception.ErrorMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 *
 * @author pho
 */
public class CampaignValidationExceptionMapper implements ExceptionMapper<CampaignValidationException> {

    private static final Logger LOG = Logger.getLogger(CampaignValidationExceptionMapper.class.getName());

    @Override
    public Response toResponse(CampaignValidationException ex) {
        LOG.log(Level.SEVERE, ex.getMessage(), ex);

        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorMessage(ex.getMessage(), Response.Status.CONFLICT.getStatusCode()))
                .build();
    }
}
