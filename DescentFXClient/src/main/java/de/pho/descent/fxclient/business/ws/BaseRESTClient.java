package de.pho.descent.fxclient.business.ws;

import de.pho.descent.fxclient.business.config.Configuration;
import de.pho.descent.shared.auth.ParamValue;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author pho
 */
public class BaseRESTClient {

    private static final String DEFAULT_HOST = "http://localhost:8080";
    private static final String DEFAULT_WS_URI = "server/rest";

    public static URI getBaseUri() {
        return getBaseUriBuilder().build();
    }

    public static URI getSecuredBaseUri() {
        return getBaseUriBuilder().path(ParamValue.SECURED_URL).build();
    }

    private static UriBuilder getBaseUriBuilder() {
        String host = Configuration.get(ParamValue.HOST);
        if (host == null || host.isEmpty()) {
            host = DEFAULT_HOST;
        }

        return UriBuilder.fromUri(host).path(DEFAULT_WS_URI);
    }
}
