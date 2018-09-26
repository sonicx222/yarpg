package de.pho.descent.web.cheat;

import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsCheatBox;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.web.campaign.CampaignController;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.quest.QuestValidationException;
import de.pho.descent.web.service.PersistenceService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author pho
 */
@Stateless
//@Path(ParamValue.SECURED_URL + "/cheat")
@Path("/cheat")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CheatBoundary {

    private static final Logger LOG = Logger.getLogger(CheatBoundary.class.getName());

    @Inject
    private PersistenceService persistenceService;

    @Inject
    private CampaignController campaignController;

    @POST
    @Path("/endActiveQuest")
    public Response endActiveQuest(WsCheatBox cheatBox) throws QuestValidationException, NotFoundException {

        Campaign campaign = persistenceService.find(Campaign.class, cheatBox.getCampaignId());
        if (campaign == null) {
            throw new NotFoundException("Campaign with id " + cheatBox.getCampaignId() + " not found!");
        }

        // prevent lazy load exception
        campaign.getHeroSelections().size();
        campaign.getHeroes().size();

        QuestEncounter encounter = campaign.getActiveQuest();
        encounter.getMonsters().size();
        encounter.getToken().size();

        // set winning side
        encounter.setWinner(cheatBox.getWinner());
        campaign = campaignController.endActiveQuest(campaign);

        LOG.log(Level.INFO, "Cheat used => endActiveQuest for campaign id {0}", cheatBox.getCampaignId());

        return Response.ok().entity(WsCampaign.createInstance(campaign)).build();
    }

    @POST
    @Path("/setWeapon")
    public Response setWeapon(WsCheatBox cheatBox) throws NotFoundException {

        GameHero targetHero = persistenceService.find(GameHero.class, cheatBox.getHeroId());
        if (targetHero == null) {
            throw new NotFoundException("Hero with id " + cheatBox.getHeroId() + " not found!");
        }

        targetHero.setWeapon(cheatBox.getItem());
        LOG.log(Level.INFO, "Cheat used => setWeapon for hero id {0}", cheatBox.getHeroId());

        return Response.ok().build();
    }

    @POST
    @Path("/addItem")
    public Response addItem(WsCheatBox cheatBox) throws NotFoundException {

        GameHero targetHero = persistenceService.find(GameHero.class, cheatBox.getHeroId());
        if (targetHero == null) {
            throw new NotFoundException("Hero with id " + cheatBox.getHeroId() + " not found!");
        }

        targetHero.getInventory().add(cheatBox.getItem());
        LOG.log(Level.INFO, "Cheat used => addItem for hero id {0}", cheatBox.getHeroId());

        return Response.ok().build();
    }
}
