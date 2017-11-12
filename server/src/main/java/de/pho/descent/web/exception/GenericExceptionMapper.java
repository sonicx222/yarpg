package de.pho.descent.web.exception;

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
        Throwable rootCause = getCause(th);
        LOG.log(Level.SEVERE, rootCause.getMessage(), rootCause);

        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorMessage(rootCause.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                .build();
    }

    private Throwable getCause(Throwable e) {
        Throwable cause = null;
        Throwable result = e;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

}
