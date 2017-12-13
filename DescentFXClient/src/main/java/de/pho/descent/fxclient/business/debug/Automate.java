package de.pho.descent.fxclient.business.debug;

import de.pho.descent.fxclient.MainApp;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.HeroSelectionClient;
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

            // create new campaign
            wsCampaign = CampaignClient.newCampaign(overlord);

            // Player 1 hero selection
            WsHeroSelection heroSelectionP1 = new WsHeroSelection(credentialsP1, GRISBAN, true);
            heroSelectionP1 = HeroSelectionClient.saveSelection(
                    credentialsP1,
                    SecurityTools.createHash(credentialsP1, false),
                    heroSelectionP1,
                    wsCampaign);

            // Player 2 hero selection    
            WsHeroSelection heroSelectionP2 = new WsHeroSelection(credentialsP2, JAINFAIRWOOD, true);
            heroSelectionP2 = HeroSelectionClient.saveSelection(
                    credentialsP2,
                    SecurityTools.createHash(credentialsP2, false),
                    heroSelectionP2,
                    wsCampaign);

            // start campaign
            wsCampaign = CampaignClient.startCampaign(credentialsOverlord,
                    SecurityTools.createHash(credentialsOverlord, false), wsCampaign);

        } catch (ServerException ex) {
            MainApp.showError(ex);
        }
        return wsCampaign;
    }
}
