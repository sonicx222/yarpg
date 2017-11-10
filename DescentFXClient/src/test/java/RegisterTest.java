
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.shared.model.Player;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.rules.ExpectedException.none;

/**
 *
 * @author pho
 */
@Ignore
public class RegisterTest {

    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void testRegistration() throws ServerException {
        Player rtPlayer = PlayerClient.registerPlayer("registertest", "registertest");

        assert (rtPlayer.getId() > 0);
        assert (rtPlayer.getUsername().equals("registertest"));

        expectedException.expect(ServerException.class);
        PlayerClient.registerPlayer("registertest", "registertest");
    }
}
