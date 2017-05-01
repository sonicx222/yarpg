package de.pho.descent.web.auth;

import de.pho.descent.shared.auth.SecurityTools;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 *
 * @author pho
 */
public class WSAuthenticationInterceptor {

    private static final Logger LOG = Logger.getLogger(WSAuthenticationInterceptor.class.getName());

    @Inject
    private PlayerController playerController;

    @AroundInvoke
    public Object authenticate(InvocationContext context) throws Exception {
        Object[] parameters = context.getParameters();
        String encodedAuthData;
        
        try {
            encodedAuthData = String.valueOf(parameters[0]);
            String[] authData = SecurityTools.extractDataFromAuthenticationToken(encodedAuthData);
        String decodedUser = authData[0];
        String digestHash = authData[1];
        } catch (NullPointerException npe) {
            throw new UserValidationException("No credentials sent");
        }
//        playerController.doAuthenticate(username, password, null, null);

        return context.proceed();
    }
}
