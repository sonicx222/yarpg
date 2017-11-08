
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.HeroSelectionClient;
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.Player;
import static de.pho.descent.shared.model.hero.HeroTemplate.*;
import org.junit.Test;

/**
 *
 * @author pho
 */
public class HeroSelectionTest {

    private static Player player1;
    private static Player player2;
    private static Player player3;
    private static Player player4;
    private static Player player5;

    private static WsCampaign campaign1;

    @Test
    public void test() throws ServerException {
        player1 = PlayerClient.registerPlayer("test1", "test1");
        player2 = PlayerClient.registerPlayer("test2", "test2");
        player3 = PlayerClient.registerPlayer("test3", "test3");
        player4 = PlayerClient.registerPlayer("test4", "test4");
        player5 = PlayerClient.registerPlayer("test5", "test5");

        // login
        player1 = PlayerClient.loginPlayer("test1", "test1");
        player2 = PlayerClient.loginPlayer("test2", "test2");
        player3 = PlayerClient.loginPlayer("test3", "test3");
        player4 = PlayerClient.loginPlayer("test4", "test4");
        player5 = PlayerClient.loginPlayer("test5", "test5");

        // player1 creates campaign as overlord
        campaign1 = CampaignClient.createCampaign(player1);
        System.out.println(campaign1);

        // get available campaigns
        CampaignClient.getActiveCampaigns(player1.getUsername(), player1.getPassword());

        Credentials credentialsP1 = new Credentials(player1);
        WsHeroSelection heroSelection = HeroSelectionClient.saveSelection(credentialsP1, new WsHeroSelection(player1.getUsername(), SYNDRAEL), campaign1);
        System.out.println(heroSelection);
        heroSelection.setSelectedHero(ASHRIAN);
        heroSelection = HeroSelectionClient.saveSelection(credentialsP1, heroSelection, campaign1);
        System.out.println(heroSelection);
    }
}
