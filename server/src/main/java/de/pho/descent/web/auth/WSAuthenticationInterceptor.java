package de.pho.descent.web.auth;

import de.pho.descent.web.model.User;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author pho
 */
public class WSAuthenticationInterceptor {

    private static final Logger LOG = Logger.getLogger(WSAuthenticationInterceptor.class.getName());

    @Inject
    private LoginService loginService;

    @Inject
    private User user;

    @AroundInvoke
    public Object authenticate(InvocationContext context) throws Exception {
        Object[] parameters = context.getParameters();

        String username = String.valueOf(parameters[0]);
        String password = String.valueOf(parameters[1]);

        try {
            user = loginService.doLogin(username, password);
        } catch (UserNotFoundException | UserValidationException ex) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        return context.proceed();
    }
}
