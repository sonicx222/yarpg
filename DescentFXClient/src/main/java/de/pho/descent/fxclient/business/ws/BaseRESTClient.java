package de.pho.descent.fxclient.business.ws;

import de.pho.descent.shared.auth.ParamValue;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

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
}
