package de.pho.descent.web.hero;

import de.pho.descent.shared.model.hero.HeroTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author pho
 */
@Stateless
@Path("/campaigns/{campaignId}/heroes")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
public class HeroBoundary {
    
   
    
   @GET
   public List<HeroTemplate> getAvailableHeroes() {
       List<HeroTemplate> selectionList = new ArrayList<>();     
       selectionList.addAll(Arrays.asList(HeroTemplate.values()));
       
       return selectionList;
   }
}
