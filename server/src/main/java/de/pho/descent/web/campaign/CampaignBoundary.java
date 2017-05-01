package de.pho.descent.web.campaign;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.web.auth.WSAuthenticationInterceptor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
@Stateless
@Path("secured/campaigns")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CampaignBoundary {

    @EJB
    private CampaignController campaignController;

    @GET
    @Interceptors(WSAuthenticationInterceptor.class)
    public List<Campaign> getAllCampaigns(@HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken) {
        return campaignController.getAllCampaigns();
    }

    @POST
    public Response createNewCampaign(Campaign unsavedCampaign, @Context UriInfo uriInfo) throws URISyntaxException {
        Campaign campaign = campaignController.createNewCampaign(unsavedCampaign);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(campaign.getId())).build();

        return Response.created(uri).entity(campaign).build();
    }

    @POST
    @Path("/test")
    public Response createTestCampaign() throws URISyntaxException {
        Campaign c = campaignController.createTestCampaign();
        Response response = Response.status(Status.CREATED).entity(c).build();

        return response;
    }
}
