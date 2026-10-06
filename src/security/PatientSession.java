package security;

import model.PatientIdentity;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/** Separate process-local patient session. Never stores credentials or staff roles. */
public final class PatientSession {
    public static final PatientSession INSTANCE = new PatientSession(Clock.systemUTC());
    private final Clock clock;
    private PatientIdentity current;
    public PatientSession(Clock clock) { this.clock = clock; }
    public synchronized PatientIdentity create(long patientId, String fullName) {
        current = new PatientIdentity(patientId, fullName, Instant.now(clock)); return current;
    }
    public synchronized Optional<PatientIdentity> current() { return Optional.ofNullable(current); }
    public synchronized Optional<PatientIdentity> logout() { PatientIdentity old=current; current=null; return Optional.ofNullable(old); }
    public synchronized boolean isLoggedIn() { return current != null; }
}
