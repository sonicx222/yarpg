package de.pho.descent.web.admin;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.dto.WsEntityRequest;
import de.pho.descent.web.service.PersistenceService;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Restful Service for administrative tasks
 *
 * @author pho
 */
@Stateless
@Path(ParamValue.SECURED_URL + "/admin")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class AdminBoundary {

    @Inject
    private PersistenceService persistenceService;

    @POST
    @Path("/entities")
    public Response getEntity(WsEntityRequest entityReq) throws ClassNotFoundException {
        Class entityClazz = Class.forName(entityReq.getType());
        Object entity = persistenceService.find(entityClazz, entityReq.getId());

        return Response.ok().entity(entity).build();
    }
}
