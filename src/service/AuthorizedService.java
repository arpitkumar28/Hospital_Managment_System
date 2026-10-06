package service;

import model.AuthenticatedUser;
import security.AuthorizationService;
import security.SessionManager;

import java.util.function.Supplier;

/** Shared session-backed authorization boundary for protected application services. */
abstract class AuthorizedService {
    protected final SessionManager sessions;
    protected final AuthorizationService authorization;

    protected AuthorizedService(SessionManager sessions, AuthorizationService authorization) {
        this.sessions = sessions;
        this.authorization = authorization;
    }

    protected AuthenticatedUser requireModule(String module) {
        AuthenticatedUser actor = sessions.getCurrentUser().orElse(null);
        authorization.requireModule(actor, module);
        return actor;
    }

    protected <T> T execute(String module, Supplier<T> operation) {
        requireModule(module);
        return operation.get();
    }

    protected void execute(String module, Runnable operation) {
        requireModule(module);
        operation.run();
    }
}
