package de.pho.descent.fxclient.business.debug;

import de.pho.descent.fxclient.MainApp;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.HeroSelectionClient;
import de.pho.descent.fxclient.business.ws.MapClient;
import de.pho.descent.fxclient.business.ws.MessageClient;
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.QuestClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.game.map.MapService;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.dto.WsQuestEncounter;
import de.pho.descent.shared.model.Player;
import static de.pho.descent.shared.model.hero.HeroTemplate.GRISBAN;
import static de.pho.descent.shared.model.hero.HeroTemplate.JAINFAIRWOOD;
import de.pho.descent.shared.model.map.MapField;
import java.util.List;

/**
 *
 * @author pho
 */
public class Automate {

    private static final String credentialsOverlord = "overlord";
    private static final String credentialsP1 = "player1";
    private static final String credentialsP2 = "player2";

    private static Player overlord;
    private static Player player1;
    private static Player player2;

    private static WsCampaign wsCampaign;

    public static Object[] startCampaignWithTwoHeroes() {
        try {
            // register
            overlord = PlayerClient.registerPlayer(credentialsOverlord, credentialsOverlord);
            player1 = PlayerClient.registerPlayer(credentialsP1, credentialsP1);
            player2 = PlayerClient.registerPlayer(credentialsP2, credentialsP2);

            MessageClient.postMessage(overlord, null, "I'll start a new campaign, please join and make your selections");

            // create new campaign
            wsCampaign = CampaignClient.newCampaign(overlord);

            MessageClient.postMessage(player1, null, "Ok we join...");

            // Player 1 hero selection
            WsHeroSelection heroSelectionP1 = new WsHeroSelection(credentialsP1, GRISBAN, true);
            heroSelectionP1 = HeroSelectionClient.saveSelection(
                    credentialsP1,
                    SecurityTools.createHash(credentialsP1, false),
                    heroSelectionP1,
                    wsCampaign);
            MessageClient.postMessage(player1, wsCampaign, "I made my selection!");

            // Player 2 hero selection    
            WsHeroSelection heroSelectionP2 = new WsHeroSelection(credentialsP2, JAINFAIRWOOD, true);
            heroSelectionP2 = HeroSelectionClient.saveSelection(
                    credentialsP2,
                    SecurityTools.createHash(credentialsP2, false),
                    heroSelectionP2,
                    wsCampaign);
            MessageClient.postMessage(player2, wsCampaign, "I made my selection aswell!");

            // send some campaign messages
            MessageClient.postMessage(player2, wsCampaign, "Can we start?");
            MessageClient.postMessage(overlord, wsCampaign, "I'll start!");

            // start campaign
            wsCampaign = CampaignClient.startCampaign(credentialsOverlord,
                    SecurityTools.createHash(credentialsOverlord, false), wsCampaign);

            MessageClient.postMessage(player1, wsCampaign, "yay, we started!");
            MessageClient.postMessage(overlord, null, "New Campaign started!");
            
//            WsQuestEncounter encounter = QuestClient.getQuestEncounter(credentialsOverlord,
//                    SecurityTools.createHash(credentialsOverlord, false), wsCampaign.getQuestEncounterId());
//            
//            WsGameMap map = MapClient.getQuestMap(credentialsOverlord,
//                    SecurityTools.createHash(credentialsOverlord, false), encounter.getMapId());
//            
//            MapService ms = new MapService();
//            MapField testField = wsCampaign.getGameHeroes().stream()
//                    .filter(h -> h.isActive())
//                    .findAny().get().getCurrentLocation();
//            List<MapField> fieldsInRange = ms.getFieldsInRange(testField, 3, map);

        } catch (ServerException ex) {
            MainApp.showError(ex);
        }
        
        Object[] result = new Object[2];
        result[0] = overlord;
        result[1] = wsCampaign;
        return result;
    }
}
