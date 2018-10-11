package de.pho.descent.web.cheat;

import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsCheatBox;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.web.campaign.CampaignController;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.quest.QuestController;
import de.pho.descent.web.quest.QuestValidationException;
import de.pho.descent.web.service.PersistenceService;
import java.util.ArrayList;
import java.util.List;
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

    @Inject
    private QuestController questController;

    @POST
    @Path("/setUnitLocation")
    public Response setUnitLocation(WsCheatBox cheatBox) throws NotFoundException {

        Campaign campaign = persistenceService.find(Campaign.class, cheatBox.getCampaignId());
        if (campaign == null) {
            throw new NotFoundException("Campaign with id " + cheatBox.getCampaignId() + " not found!");
        }
        
        GameUnit unit = questController.getGamUnit(cheatBox.getUnitId());

        MapField targetField = persistenceService.find(MapField.class, cheatBox.getFieldId());
        if (targetField == null) {
            throw new NotFoundException("MapField with id " + cheatBox.getFieldId() + " not found!");
        }

        // place unit to new location
        List<MapField> targetLocation = new ArrayList<>();
        unit.getCurrentLocation().forEach(field -> field.setGameUnit(null));
        targetField.setGameUnit(unit);
        targetLocation.add(targetField);
        
        if (unit instanceof GameMonster && ((GameMonster) unit).getMonsterTemplate().getFieldSize() > 1) {
            // big monster
            MapField east = campaign.getActiveQuest().getMap().getField(targetField.getxPos() + 1, targetField.getyPos());
            east.setGameUnit(unit);
            targetLocation.add(east);

            MapField southeast = campaign.getActiveQuest().getMap().getField(targetField.getxPos() + 1, targetField.getyPos() + 1);
            southeast.setGameUnit(unit);
            targetLocation.add(southeast);

            MapField south = campaign.getActiveQuest().getMap().getField(targetField.getxPos(), targetField.getyPos() + 1);
            south.setGameUnit(unit);
            targetLocation.add(south);
        }       
        unit.setCurrentLocation(targetLocation);     

        LOG.log(Level.INFO, "Cheat used => setUnitLocation for unit id {0}", cheatBox.getUnitId());

        return Response.ok().build();
    }

    @POST
    @Path("/endActiveQuest")
    public Response endActiveQuest(WsCheatBox cheatBox) throws QuestValidationException, NotFoundException {

        Campaign campaign = persistenceService.find(Campaign.class, cheatBox.getCampaignId());
        if (campaign == null) {
            throw new NotFoundException("Campaign with id " + cheatBox.getCampaignId() + " not found!");
        }

        // prevent lazy load exception
        campaign.getHeroSelections().size();
        campaign.getHeroes().forEach(hero -> hero.getCurrentLocation().size());

        QuestEncounter encounter = campaign.getActiveQuest();
        encounter.getMonsters().forEach(monster -> monster.getCurrentLocation().size());
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

        GameHero targetHero = persistenceService.find(GameHero.class, cheatBox.getUnitId());
        if (targetHero == null) {
            throw new NotFoundException("Hero with id " + cheatBox.getUnitId() + " not found!");
        }

        targetHero.setWeapon(cheatBox.getItem());
        LOG.log(Level.INFO, "Cheat used => setWeapon for hero id {0}", cheatBox.getUnitId());

        return Response.ok().build();
    }

    @POST
    @Path("/addItem")
    public Response addItem(WsCheatBox cheatBox) throws NotFoundException {

        GameHero targetHero = persistenceService.find(GameHero.class, cheatBox.getUnitId());
        if (targetHero == null) {
            throw new NotFoundException("Hero with id " + cheatBox.getUnitId() + " not found!");
        }

        targetHero.getInventory().add(cheatBox.getItem());
        LOG.log(Level.INFO, "Cheat used => addItem for hero id {0}", cheatBox.getUnitId());

        return Response.ok().build();
    }
}
