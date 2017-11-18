package de.pho.descent.web.auth;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Utility service to provide the required auth token
 * for further secured requests
 * 
 * @author pho
 */
@Path("token")
@Produces({MediaType.TEXT_PLAIN})
@Stateless
public class TokenProviderBoundary {

    @GET
    public String login(
            @HeaderParam(ParamValue.USERNAME) String username,
            @HeaderParam(ParamValue.PASSWORD) String password,
            @HeaderParam(ParamValue.HTTP_METHOD) String method,
            @HeaderParam(ParamValue.URI) String uripath) {

        return SecurityTools.createAuthenticationToken(username, SecurityTools.createHash(password, false), method, "/server/rest/" + uripath);
    }

}
