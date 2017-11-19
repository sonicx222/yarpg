
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.HeroSelectionClient;
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import static de.pho.descent.shared.model.hero.HeroTemplate.GRISBAN;
import static de.pho.descent.shared.model.hero.HeroTemplate.JAINFAIRWOOD;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import java.util.List;
import java.util.Objects;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.rules.ExpectedException.none;
import org.junit.runners.MethodSorters;

/**
 *
 * @author pho
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CampaignTest {

    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void testCreateCampaign() throws ServerException {
        final String credentials = "createcampaigntest";
        Player campTest = PlayerClient.registerPlayer(credentials, credentials);

        // campTest creates new campaign1 as overlord
        WsCampaign campaign1 = CampaignClient.newCampaign(campTest);

        assert (campaign1.getId() > 0);
        assert (campaign1.getPhase() == CampaignPhase.HERO_SELECTION);
        assert (campaign1.getActiveQuest() == Quest.FIRST_BLOOD);
        assert (campaign1.getPart() == QuestPart.FIRST);
        assert (Objects.equals(campaign1.getOverlord(), campTest.getUsername()));

        // campTest creates campaign2 as overlord
        WsCampaign campaign2 = CampaignClient.newCampaign(campTest);
        assert (campaign2.getId() > campaign1.getId());

        // wrong user
        String wrongUser = "WrongUser";
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("No entity found for query");
        CampaignClient.newCampaign(wrongUser, credentials);

        // wrong pwd
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Login failed! Check Password");
        CampaignClient.newCampaign(credentials, "WronPwd");
    }

    @Test
    public void testStartCampaign() throws ServerException {
        final String credentials1 = "startcampaigntest1";
        final String credentials2 = "startcampaigntest2";
        final String credentials3 = "startcampaigntest3";
        Player startTest1 = PlayerClient.registerPlayer(credentials1, credentials1);
        Player startTest2 = PlayerClient.registerPlayer(credentials2, credentials2);
        Player startTest3 = PlayerClient.registerPlayer(credentials3, credentials3);

        // campTest creates new campaign1 as overlord
        WsCampaign campaign1 = CampaignClient.newCampaign(startTest1);

        WsHeroSelection heroSelectionP2 = new WsHeroSelection(credentials2, GRISBAN);
        WsHeroSelection heroSelectionP3 = new WsHeroSelection(credentials3, JAINFAIRWOOD);
        heroSelectionP2 = HeroSelectionClient.saveSelection(credentials2, credentials2, heroSelectionP2, campaign1);
        heroSelectionP3 = HeroSelectionClient.saveSelection(credentials3, credentials3, heroSelectionP3, campaign1);

        heroSelectionP2.setReady(true);
        heroSelectionP2 = HeroSelectionClient.saveSelection(credentials2, credentials2, heroSelectionP2, campaign1);

        heroSelectionP3.setReady(true);
        heroSelectionP3 = HeroSelectionClient.saveSelection(credentials3, credentials3, heroSelectionP3, campaign1);

        List<WsHeroSelection> selections = HeroSelectionClient.getCurrentSelections(credentials3,
                SecurityTools.createHash(credentials3, false), campaign1);
        System.out.println("Selections:\n");
        for (WsHeroSelection hs : selections) {
            System.out.println(hs);
        }

        // start campaign
        campaign1 = CampaignClient.startCampaign(credentials1,
                SecurityTools.createHash(credentials1, false), campaign1);
    }
}
