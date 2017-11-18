
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import java.util.Objects;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.rules.ExpectedException.none;
import org.junit.runners.MethodSorters;

/**
 *
 * @author pho
 */
@Ignore
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
}
