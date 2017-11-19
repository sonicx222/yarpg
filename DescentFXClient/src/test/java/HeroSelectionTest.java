
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.HeroSelectionClient;
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import static de.pho.descent.shared.model.hero.HeroTemplate.*;
import java.util.List;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.rules.ExpectedException.none;
import org.junit.runners.MethodSorters;
import static org.assertj.core.api.Assertions.*;
import org.junit.BeforeClass;

/**
 *
 * @author pho
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HeroSelectionTest {

    private static final String credentialsP1 = "player1";
    private static final String credentialsP2 = "player2";
    private static final String credentialsP3 = "player3";
    private static final String credentialsP4 = "player4";
    private static final String credentialsP5 = "player5";
    private static final String credentialsP6 = "player6";

    private static Player player1;
    private static Player player2;
    private static Player player3;
    private static Player player4;
    private static Player player5;
    private static Player player6;

    private static WsCampaign campaign1;
    private static WsCampaign campaign2;
    private static WsCampaign campaign3;
    private static WsCampaign campaign4;
    private static WsCampaign campaign5;
    private static WsCampaign campaign6;
    private static WsCampaign campaign7;

    @Rule
    public ExpectedException expectedException = none();

    @BeforeClass
    public static void init() throws ServerException {
        player1 = PlayerClient.registerPlayer(credentialsP1, credentialsP1);
        player2 = PlayerClient.registerPlayer(credentialsP2, credentialsP2);
        player3 = PlayerClient.registerPlayer(credentialsP3, credentialsP3);
        player4 = PlayerClient.registerPlayer(credentialsP4, credentialsP4);
        player5 = PlayerClient.registerPlayer(credentialsP5, credentialsP5);
        player6 = PlayerClient.registerPlayer(credentialsP6, credentialsP6);
    }

    @Test
    public void testHeroSelectionCampaign1() throws ServerException {
        // player1 creates new campaign1 as overlord
        campaign1 = CampaignClient.newCampaign(player1);

        // campaign 1, full group on selection stage
        WsHeroSelection heroSelectionP2 = new WsHeroSelection(credentialsP2, GRISBAN);
        WsHeroSelection heroSelectionP3 = new WsHeroSelection(credentialsP3, JAINFAIRWOOD);
        WsHeroSelection heroSelectionP4 = new WsHeroSelection(credentialsP4, ASHRIAN);
        WsHeroSelection heroSelectionP5 = new WsHeroSelection(credentialsP5, LEORICOFTHEBOOK);
        WsHeroSelection heroSelectionP6 = new WsHeroSelection(credentialsP6, WIDOWTARHA);
        heroSelectionP2 = HeroSelectionClient.saveSelection(credentialsP2, credentialsP2, heroSelectionP2, campaign1);
        heroSelectionP3 = HeroSelectionClient.saveSelection(credentialsP3, credentialsP3, heroSelectionP3, campaign1);
        heroSelectionP4 = HeroSelectionClient.saveSelection(credentialsP4, credentialsP4, heroSelectionP4, campaign1);
        heroSelectionP5 = HeroSelectionClient.saveSelection(credentialsP5, credentialsP5, heroSelectionP5, campaign1);

        // check available campaigns for different users
        List<WsCampaign> activeCampaigns = CampaignClient.getActiveCampaigns(player1.getUsername(), player1.getPassword());
        assertThat(activeCampaigns)
                .extracting("id").contains(campaign1.getId());

        activeCampaigns = CampaignClient.getActiveCampaigns(player3.getUsername(), player3.getPassword());
        assertThat(activeCampaigns)
                .extracting("id").contains(campaign1.getId());

        activeCampaigns = CampaignClient.getActiveCampaigns(player6.getUsername(), player6.getPassword());
        assertThat(activeCampaigns).extracting("id").doesNotContain(campaign1.getId());

        // check: full group exception
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Hero selection not possible. Group is full");
        HeroSelectionClient.saveSelection(credentialsP6, credentialsP6, heroSelectionP6, campaign1);
    }

    @Test
    public void testHeroSelectionCampaign2() throws ServerException {
        // player2 creates new campaign2 as overlord
        campaign2 = CampaignClient.newCampaign(player2);

        // campaign 2, full group on selection stage WITH user 'P1'
        WsHeroSelection heroSelectionP1 = new WsHeroSelection(credentialsP1, GRISBAN);
        WsHeroSelection heroSelectionP3 = new WsHeroSelection(credentialsP3, JAINFAIRWOOD);
        WsHeroSelection heroSelectionP4 = new WsHeroSelection(credentialsP4, ASHRIAN);
        WsHeroSelection heroSelectionP5 = new WsHeroSelection(credentialsP5, LEORICOFTHEBOOK);
        heroSelectionP1 = HeroSelectionClient.saveSelection(credentialsP1, credentialsP1, heroSelectionP1, campaign2);
        heroSelectionP3 = HeroSelectionClient.saveSelection(credentialsP3, credentialsP3, heroSelectionP3, campaign2);
        heroSelectionP4 = HeroSelectionClient.saveSelection(credentialsP4, credentialsP4, heroSelectionP4, campaign2);
        heroSelectionP5 = HeroSelectionClient.saveSelection(credentialsP5, credentialsP5, heroSelectionP5, campaign2);

        // check available campaigns for different users
        List<WsCampaign> activeCampaigns = CampaignClient.getActiveCampaigns(player1.getUsername(), player1.getPassword());
        assertThat(activeCampaigns)
                .extracting("id").contains(campaign1.getId(), campaign2.getId());

        activeCampaigns = CampaignClient.getActiveCampaigns(player2.getUsername(), player2.getPassword());
        assertThat(activeCampaigns)
                .extracting("id").contains(campaign1.getId(), campaign2.getId());
    }

    @Test
    public void testHeroSelectionCampaign3() throws ServerException {
        // player1 creates new campaign3 as overlord
        campaign3 = CampaignClient.newCampaign(player1);

        // campaign 3, 2-man group on selection stage
        WsHeroSelection heroSelectionP1 = new WsHeroSelection(credentialsP1, TOMBLEBURROWELL);
        WsHeroSelection heroSelectionP2 = new WsHeroSelection(credentialsP2, GRISBAN);
        WsHeroSelection heroSelectionP3 = new WsHeroSelection(credentialsP3, JAINFAIRWOOD);

        heroSelectionP2 = HeroSelectionClient.saveSelection(credentialsP2, credentialsP2, heroSelectionP2, campaign3);
        heroSelectionP3 = HeroSelectionClient.saveSelection(credentialsP3, credentialsP3, heroSelectionP3, campaign3);

        // check available campaigns for different users
        List<WsCampaign> activeCampaigns = CampaignClient.getActiveCampaigns(player1.getUsername(), player1.getPassword());
        assertThat(activeCampaigns)
                .extracting("id").contains(campaign1.getId(), campaign2.getId(), campaign3.getId());

        // check: overlord can not select
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Overlord can not take part in hero selection");
        HeroSelectionClient.saveSelection(credentialsP1, credentialsP1, heroSelectionP1, campaign1);
    }

    @Test
    public void testHeroSelectionCampaign4() throws ServerException {
        // player2 creates new campaign4 as overlord
        campaign4 = CampaignClient.newCampaign(player2);

        // campaign 4, 2-man group on selection stage WITH user 'P1'
        WsHeroSelection heroSelectionP1 = new WsHeroSelection(credentialsP1, GRISBAN);
        WsHeroSelection heroSelectionP3 = new WsHeroSelection(credentialsP3, JAINFAIRWOOD);
        heroSelectionP1 = HeroSelectionClient.saveSelection(credentialsP1, credentialsP1, heroSelectionP1, campaign4);
        heroSelectionP3 = HeroSelectionClient.saveSelection(credentialsP3, credentialsP3, heroSelectionP3, campaign4);

        List<WsCampaign> activeCampaigns = CampaignClient.getActiveCampaigns(player1.getUsername(), player1.getPassword());
        assertThat(activeCampaigns)
                .extracting("id")
                .contains(campaign1.getId(), campaign2.getId(), campaign3.getId(), campaign4.getId());

        // check: hero already selected exception
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Hero " + JAINFAIRWOOD.getName() + " already selected");
        WsHeroSelection heroSelectionP6 = new WsHeroSelection(credentialsP6, JAINFAIRWOOD);
        HeroSelectionClient.saveSelection(credentialsP6, credentialsP6, heroSelectionP6, campaign4);
    }

    @Test
    public void testHeroSelectionCampaign5() throws ServerException {
        // player1 creates campaign5 as overlord, campaign5 in stage ENCOUNTER
        campaign5 = CampaignClient.createCampaign(player1,
                new WsCampaign(player1.getUsername(), CampaignPhase.ENCOUNTER, null, null));

        // check: game already started error
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Campaign not in phase: " + CampaignPhase.HERO_SELECTION.name());
        WsHeroSelection heroSelectionP6 = new WsHeroSelection(credentialsP6, JAINFAIRWOOD);
        HeroSelectionClient.saveSelection(credentialsP6, credentialsP6, heroSelectionP6, campaign5);
    }

    @Test
    public void testHeroSelectionCampaign6() throws ServerException {
        // player2 creates campaign6 as overlord, campaign in stage TRAVEL
        campaign6 = CampaignClient.createCampaign(player2,
                new WsCampaign(player1.getUsername(), CampaignPhase.TRAVEL, null, null));

        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Campaign not in phase: " + CampaignPhase.HERO_SELECTION.name());
        WsHeroSelection heroSelectionP6 = new WsHeroSelection(credentialsP6, JAINFAIRWOOD);
        HeroSelectionClient.saveSelection(credentialsP6, credentialsP6, heroSelectionP6, campaign6);
    }

    @Test
    public void testHeroSelectionCampaign7() throws ServerException {
        // player2 creates campaign7 as overlord, campaign in stage FINISHED
        campaign7 = CampaignClient.createCampaign(player2,
                new WsCampaign(player1.getUsername(), CampaignPhase.FINISHED, null, null));

        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Campaign not in phase: " + CampaignPhase.HERO_SELECTION.name());
        WsHeroSelection heroSelectionP6 = new WsHeroSelection(credentialsP6, JAINFAIRWOOD);
        HeroSelectionClient.saveSelection(credentialsP6, credentialsP6, heroSelectionP6, campaign7);
    }

    @Test
    public void testHeroSelectionCampaign8() throws ServerException {
        // wrong campaign id
        WsCampaign falseCampaign = new WsCampaign(player6.getUsername(), CampaignPhase.ENCOUNTER, null, null);
        falseCampaign.setId(666L);
        WsHeroSelection heroSelectionP6 = new WsHeroSelection(credentialsP6, JAINFAIRWOOD);

        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Campaign with Id: " + falseCampaign.getId() + " not found");
        HeroSelectionClient.saveSelection(credentialsP6, credentialsP6, heroSelectionP6, falseCampaign);
    }
}
