package de.pho.descent.web.auth;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.model.Player;
import java.net.URI;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author pho
 */
@Path("players")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Stateless
public class PlayerBoundary {

    private static final Logger LOG = Logger.getLogger(PlayerBoundary.class.getName());

    @EJB
    private PlayerController playerController;

    @POST
    public Response register(
            @HeaderParam(ParamValue.USERNAME) String username,
            @HeaderParam(ParamValue.ENCRYPTED_PWD) String password,
            @Context UriInfo uriInfo)
            throws PlayerAlreadyExistsException {
        Player player = playerController.createPlayer(username, password);
        URI uri = uriInfo.getAbsolutePathBuilder().path(player.getUsername()).build();

        return Response.created(uri).entity(player).build();
    }

    @GET
    @Path("/{" + ParamValue.USERNAME + "}")
    public Response login(
            @PathParam(ParamValue.USERNAME) String username,
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String encodedAuthData,
            @Context UriInfo uriInfo)
            throws UserValidationException {

        String[] authData = SecurityTools.extractDataFromAuthHeaderValue(encodedAuthData);
        String decodedUser = authData[0];
        String digestHash = authData[1];

        if (!username.equals(decodedUser)) {
            throw new UserValidationException("Wrong username");
        }

        String uriPath = uriInfo.getRequestUri().getPath();
        Player player = playerController.doLogin(username, HttpMethod.GET, uriPath, digestHash);

        return Response.accepted(player).build();
    }

    @PUT
    @Path("/{" + ParamValue.USERNAME + "}")
    @Interceptors(WSAuthenticationInterceptor.class)
    public Response updatePlayer(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken,
            @PathParam(ParamValue.USERNAME) String username, Player player) {
        Response response;
        if (SecurityTools.extractDataFromAuthHeaderValue(authToken)[0].equals(username)) {
            player.setUsername(username);
            Player updatedPlayer = playerController.updatePlayer(player);
            response = Response.accepted(updatedPlayer).build();
        } else {
            response = Response.status(Status.UNAUTHORIZED).build();
        }

        return response;
    }
}
