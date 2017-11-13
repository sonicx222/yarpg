
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.HeroSelectionClient;
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import static de.pho.descent.shared.model.hero.HeroTemplate.*;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import java.util.Objects;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.rules.ExpectedException.none;

/**
 *
 * @author pho
 */
public class CampaignTest {

    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void testCreateCampaign() throws ServerException {
        final String credentials = "createcampaigntest";
        Player campTest = PlayerClient.registerPlayer(credentials, credentials);

        // player1 creates campaign1 as overlord
        WsCampaign campaign1 = CampaignClient.createCampaign(campTest);

        assert (campaign1.getId() > 0);
        assert (campaign1.getPhase() == CampaignPhase.HERO_SELECTION);
        assert (campaign1.getActiveQuest().getQuest() == Quest.FIRST_BLOOD);
        assert (campaign1.getActiveQuest().getPart() == QuestPart.FIRST);
        assert (Objects.equals(campaign1.getOverlord().getUsername(), campTest.getUsername()));

        // player1 creates campaign1 as overlord
        WsCampaign campaign2 = CampaignClient.createCampaign(campTest);
        assert (campaign2.getId() > campaign1.getId());

        // wrong user
        String wrongUser = "WrongUser";
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("No entity found for query");
        CampaignClient.createCampaign(wrongUser, credentials);

        // wrong pwd
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Login failed! Check Password");
        CampaignClient.createCampaign(credentials, "WronPwd");
    }

    @Test
    public void testHeroSelection() throws ServerException {
        final String credentialsP1 = "player1";
        final String credentialsP2 = "player2";
        final String credentialsP3 = "player3";
        final String credentialsP4 = "player4";
        final String credentialsP5 = "player5";

        Player player1 = PlayerClient.registerPlayer(credentialsP1, credentialsP1);
        Player player2 = PlayerClient.registerPlayer(credentialsP2, credentialsP2);
        Player player3 = PlayerClient.registerPlayer(credentialsP3, credentialsP3);
        Player player4 = PlayerClient.registerPlayer(credentialsP4, credentialsP4);
        Player player5 = PlayerClient.registerPlayer(credentialsP5, credentialsP5);

        // player1 creates campaign as overlord
        WsCampaign campaign1 = CampaignClient.createCampaign(player1);
        // player2 creates campaign as overlord
        WsCampaign campaign2 = CampaignClient.createCampaign(player2);
        WsCampaign campaign3 = CampaignClient.createCampaign(player1);
        // player2 creates campaign as overlord
        WsCampaign campaign4 = CampaignClient.createCampaign(player2);
        WsCampaign campaign5 = CampaignClient.createCampaign(player1);
        // player2 creates campaign as overlord
        WsCampaign campaign6 = CampaignClient.createCampaign(player2);
        // player2 creates campaign as overlord
        WsCampaign campaign7 = CampaignClient.createCampaign(player2);

        // check available campaigns for different users
        CampaignClient.getActiveCampaigns(player1.getUsername(), player1.getPassword());

        /**
         * HEROSELECTION
         */
        // campaign 1, full group on selection stage
        WsHeroSelection heroSelectionP2 = new WsHeroSelection(credentialsP2, GRISBAN);
        WsHeroSelection heroSelectionP3 = new WsHeroSelection(credentialsP3, JAINFAIRWOOD);
        WsHeroSelection heroSelectionP4 = new WsHeroSelection(credentialsP4, ASHRIAN);
        WsHeroSelection heroSelectionP5 = new WsHeroSelection(credentialsP5, LEORICOFTHEBOOK);
        heroSelectionP2 = HeroSelectionClient.saveSelection(credentialsP2, credentialsP2, heroSelectionP2, campaign1);
        heroSelectionP3 = HeroSelectionClient.saveSelection(credentialsP3, credentialsP3, heroSelectionP3, campaign1);
        heroSelectionP4 = HeroSelectionClient.saveSelection(credentialsP4, credentialsP4, heroSelectionP4, campaign1);
        heroSelectionP5 = HeroSelectionClient.saveSelection(credentialsP5, credentialsP5, heroSelectionP5, campaign1);

        // campaign 2, full group on selection stage WITH user 'P1'
        WsHeroSelection heroSelectionP1 = new WsHeroSelection(credentialsP1, GRISBAN);
        heroSelectionP1 = HeroSelectionClient.saveSelection(credentialsP1, credentialsP1, heroSelectionP1, campaign2);
        heroSelectionP3 = HeroSelectionClient.saveSelection(credentialsP3, credentialsP3, heroSelectionP3, campaign2);
        heroSelectionP4 = HeroSelectionClient.saveSelection(credentialsP4, credentialsP4, heroSelectionP4, campaign2);
        heroSelectionP5 = HeroSelectionClient.saveSelection(credentialsP5, credentialsP5, heroSelectionP5, campaign2);

        // campaign 3, 2-man group on selection stage
        heroSelectionP2 = HeroSelectionClient.saveSelection(credentialsP2, credentialsP2, heroSelectionP2, campaign3);
        heroSelectionP3 = HeroSelectionClient.saveSelection(credentialsP3, credentialsP3, heroSelectionP3, campaign3);

        // campaign 4, 2-man group on selection stage WITH user 'P1'
        heroSelectionP1 = HeroSelectionClient.saveSelection(credentialsP1, credentialsP1, heroSelectionP1, campaign4);
        heroSelectionP3 = HeroSelectionClient.saveSelection(credentialsP3, credentialsP3, heroSelectionP3, campaign4);
    }
}
