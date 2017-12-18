package de.pho.descent.web.message;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.dto.WsMessage;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.web.exception.NotFoundException;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author pho
 */
@Stateless
@Path(ParamValue.SECURED_URL + "/messages")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class MessageBoundary {

    private static final Logger LOG = Logger.getLogger(MessageBoundary.class.getName());

    @EJB
    private MessageController messageController;

    @GET
    public Response getGeneralMessages() {
        LOG.log(Level.INFO, "Calling getGeneralMessages()");

        List<WsMessage> dtoMessages = messageController.getGeneralMessages();
        GenericEntity<List<WsMessage>> list = new GenericEntity<List<WsMessage>>(dtoMessages) {
        };

        return Response.ok().entity(list).build();
    }

    @GET
    @Path("/campaign/{" + ParamValue.CAMPAIGN_ID + "}")
    public Response getMessagesByCampaign(
            @PathParam(ParamValue.CAMPAIGN_ID) long campaignId) {
        LOG.log(Level.INFO, "Calling getMessagesByCampaign()");

        List<WsMessage> dtoMessages = messageController.getMessagesByCampaignId(campaignId);
        GenericEntity<List<WsMessage>> list = new GenericEntity<List<WsMessage>>(dtoMessages) {
        };

        return Response.ok().entity(list).build();
    }

    @POST
    public Response saveMessage(
            @Context UriInfo uriInfo,
            WsMessage wsMessage)
            throws NotFoundException {
        LOG.log(Level.INFO, "Calling saveMessage()");

        Message savedMessage = messageController.saveMessage(wsMessage);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(savedMessage.getId())).build();

        return Response.created(uri).entity(WsMessage.createInstance(savedMessage)).build();
    }
}
