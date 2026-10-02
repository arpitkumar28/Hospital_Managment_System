package security;

import model.AuthenticatedUser;
import model.User;
import model.UserRole;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/** Process-local desktop session; only non-secret account identity is retained. */
public final class SessionManager {
    public static final SessionManager INSTANCE = new SessionManager(Clock.systemUTC());

    private final Clock clock;
    private AuthenticatedUser currentUser;

    public SessionManager(Clock clock) {
        this.clock = clock;
    }

    public synchronized AuthenticatedUser createSession(User user) {
        currentUser = new AuthenticatedUser(user.userId(), user.username(), user.fullName(),
                user.role(), Instant.now(clock));
        return currentUser;
    }

    public synchronized Optional<AuthenticatedUser> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public synchronized Optional<UserRole> getCurrentRole() {
        return Optional.ofNullable(currentUser).map(AuthenticatedUser::role);
    }

    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public synchronized Optional<AuthenticatedUser> logout() {
        AuthenticatedUser previous = currentUser;
        currentUser = null;
        return Optional.ofNullable(previous);
    }
}
