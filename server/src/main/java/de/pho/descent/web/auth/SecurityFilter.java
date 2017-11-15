package de.pho.descent.web.auth;

import de.pho.descent.web.player.PlayerController;
import static de.pho.descent.shared.auth.ParamValue.*;
import de.pho.descent.shared.exception.ErrorMessage;
import de.pho.descent.shared.model.Player;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Webfilter to parse URI of request and triggers of defined keyword in path. If
 * keyword is found, the value (auth token) of the Authorization header is being
 * handled, and the user together with the hashed password is being extracted
 * and checked for acceptance.
 *
 * @author pho
 */
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(SecurityFilter.class.getName());

    @Inject
    private PlayerController playerController;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // trigger
        if (requestContext.getUriInfo().getPath().contains(SECURED_URL)) {
            List<String> authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER_KEY);
            ErrorMessage errorMsg = null;

            try {
                if (authHeader == null) {
                    throw new UserValidationException("Access not allowed");
                } else if (authHeader != null && !authHeader.isEmpty()) {
                    String uriPath = requestContext.getUriInfo().getRequestUri().getPath();
                    Player p = playerController.doAuthenticate(requestContext.getMethod(), uriPath, authHeader.get(0));

                    if (p != null) {
                        return;
                    }
                }
            } catch (UserValidationException ex) {
                errorMsg = new ErrorMessage(ex.getMessage(), Response.Status.UNAUTHORIZED.getStatusCode());
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
            Response unauthorizedStatus = Response
                    .status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(errorMsg)
                    .build();

            requestContext.abortWith(unauthorizedStatus);
        }

    }
}
