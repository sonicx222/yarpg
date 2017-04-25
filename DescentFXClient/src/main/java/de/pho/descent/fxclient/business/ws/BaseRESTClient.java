package de.pho.descent.fxclient.business.ws;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.exception.ErrorMessage;
import java.net.URI;
import javafx.geometry.Pos;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.controlsfx.control.Notifications;

/**
 *
 * @author pho
 */
public class BaseRESTClient {

    private static final String WS_URI = "http://localhost:8080/server/rest";

    public static URI getBaseUri() {
        return UriBuilder.fromUri(WS_URI).build();
    }
    
        public static URI getSecuredBaseUri() {
        return UriBuilder.fromUri(WS_URI).path(ParamValue.SECURED_URL).build();
    }

    protected static void showError(Response response) {
        ErrorMessage errorMsg = response.readEntity(ErrorMessage.class);

        Notifications tmp = Notifications.create();
        tmp.darkStyle().position(Pos.TOP_RIGHT).text(errorMsg.getErrorMessage()).showError();
    }
}
