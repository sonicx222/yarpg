package de.pho.descent.web.player;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.model.Player;
import de.pho.descent.web.auth.UserValidationException;
import de.pho.descent.web.exception.NotFoundException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    @Inject
    private PlayerController playerController;

    @POST
    public Response register(
            @HeaderParam(ParamValue.USERNAME) String username,
            @HeaderParam(ParamValue.PASSWORD) String password,
            @Context UriInfo uriInfo)
            throws PlayerAlreadyExistsException {

        Player player = playerController.createPlayer(username, password);
        LOG.log(Level.INFO, "Player {0} successfully registered", username);
        URI uri = uriInfo.getAbsolutePathBuilder().path(player.getUsername()).build();

        return Response.created(uri).entity(player).build();
    }

    @GET
    @Path("/" + ParamValue.SECURED_URL + "/{" + ParamValue.USERNAME + "}")
    public Response getPlayer(
            @PathParam(ParamValue.USERNAME) String username,
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken)
            throws UserValidationException, NotFoundException {

        LOG.log(Level.INFO, "Login in: User {0}", username);
        Player player = playerController.getPlayerByToken(authToken);

        return Response.accepted(player).build();
    }

    @PUT
    @Path("/" + ParamValue.SECURED_URL + "/{" + ParamValue.USERNAME + "}")
    public Response updatePlayer(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken,
            @PathParam(ParamValue.USERNAME) String username, Player player)
            throws UserValidationException {

        Response response;
        if (SecurityTools.extractDataFromAuthenticationToken(authToken)[0].equals(username)
                && player.getUsername().equals(username)) {
            player.setUsername(username);
            Player updatedPlayer = playerController.updatePlayer(player);
            response = Response.ok(updatedPlayer).build();
        } else {
            throw new UserValidationException("Player resource mismatch with credentials or provided data");
        }

        return response;
    }
}
