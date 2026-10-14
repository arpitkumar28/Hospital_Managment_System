package model;

import java.time.LocalDate;

/** Optional admission choice scoped to the selected billing patient. */
public record BillingAdmission(int admissionId, LocalDate admissionDate, LocalDate dischargeDate) {
    public boolean isNone() {
        return admissionId <= 0;
    }

    @Override
    public String toString() {
        if (isNone()) return "No admission (optional)";
        String state = dischargeDate == null ? "active" : "discharged " + dischargeDate;
        return "Admission #" + admissionId + " · " + admissionDate + " · " + state;
    }
}
