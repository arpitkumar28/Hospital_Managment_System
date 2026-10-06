package model;

import java.time.Instant;

/** Patient session identity, separate from staff roles and containing no credentials. */
public record PatientIdentity(long patientId, String fullName, Instant loginTime) { }
