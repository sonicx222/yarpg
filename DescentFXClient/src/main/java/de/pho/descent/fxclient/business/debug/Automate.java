package de.pho.descent.fxclient.business.debug;

import de.pho.descent.fxclient.MainApp;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.HeroSelectionClient;
import de.pho.descent.fxclient.business.ws.MessageClient;
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.Player;
import static de.pho.descent.shared.model.hero.HeroTemplate.GRISBAN;
import static de.pho.descent.shared.model.hero.HeroTemplate.JAINFAIRWOOD;

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

    public static WsCampaign startCampaignWithTwoHeroes() {
        try {
            // register
            overlord = PlayerClient.loginPlayer(credentialsOverlord, credentialsOverlord);
            player1 = PlayerClient.registerPlayer(credentialsP1, credentialsP1);
            player2 = PlayerClient.registerPlayer(credentialsP2, credentialsP2);

            MessageClient.postMessage(overlord, null, "I'll start a new campaign, please joind and make your selections");
            
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
            MessageClient.postMessage(overlord, null, "New Campaign already started!, Sorry for all who were late");

        } catch (ServerException ex) {
            MainApp.showError(ex);
        }
        return wsCampaign;
    }
}
