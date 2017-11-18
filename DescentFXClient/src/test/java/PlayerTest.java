
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.model.Player;
import java.util.Objects;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.rules.ExpectedException.none;

/**
 *
 * @author pho
 */
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlayerTest {

    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void testRegistration() throws ServerException {
        final String credentials = "registertest";
        Player rtPlayer = PlayerClient.registerPlayer(credentials, credentials);

        assert (rtPlayer.getId() > 0);
        assert (rtPlayer.getUsername().equals(credentials));

        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Player " + credentials + " already exists");
        PlayerClient.registerPlayer(credentials, credentials);
    }

    @Test
    public void testLogin() throws ServerException {
        final String credentials = "logintest";
        Player rtPlayer = PlayerClient.registerPlayer(credentials, credentials);

        // login
        Player ltPlayer = PlayerClient.loginPlayer(credentials, credentials);
        assert (ltPlayer.getId() > 0);
        assert (ltPlayer.getUsername().equals(credentials));
        assert (ltPlayer.equals(rtPlayer));

        // wrong user
        String wrongUser = "WrongUser";
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("No entity found for query");
        PlayerClient.loginPlayer(wrongUser, credentials);
        
        // wrong pwd
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("Login failed! Check Password");
        PlayerClient.loginPlayer(credentials, "WrongPwd");
    }

    @Test
    public void testDeactiveLogin() throws ServerException {
        final String credentials = "deactivetest";
        Player player = PlayerClient.registerPlayer(credentials, credentials);

        // deactivate
        player.setDeactive(true);
        Player deactivePlayer = PlayerClient.updatePlayer(
                credentials, SecurityTools.createHash(credentials, false), player);

        assert (Objects.equals(player.getId(), deactivePlayer.getId()));
        assert (Objects.equals(player.getUsername(), deactivePlayer.getUsername()));
        assert (deactivePlayer.isDeactive());

        // access deactive player
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("User is deactive");
        PlayerClient.loginPlayer(credentials, credentials);
        
        // try activate player with different user
        final String credentials2 = "failupdate";
        Player failupdatePlayer = PlayerClient.registerPlayer(credentials2, credentials2);
        deactivePlayer.setDeactive(false);
        
        expectedException.expectMessage("Player resource mismatch with credentials or provided data");
        PlayerClient.updatePlayer(
                credentials2, SecurityTools.createHash(credentials2, false), deactivePlayer);
    }
}
