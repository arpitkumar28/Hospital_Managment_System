package model;

/** Patient choice shown to authorized billing staff. */
public record BillingPatient(int patientId, String name) {
    public boolean isPlaceholder() {
        return patientId <= 0;
    }

    @Override
    public String toString() {
        return isPlaceholder() ? name : name + " · Patient #" + patientId;
    }
}
