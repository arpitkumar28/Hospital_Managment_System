# Desktop application authorization

The Java Swing application checks the current `SessionManager` identity in the
service layer before invoking a DAO. The desktop navigation uses the same
module policy to hide areas; navigation visibility is not the security
boundary.

| Area | Allowed roles |
| --- | --- |
| Dashboard navigation | ADMIN, RECEPTIONIST, DOCTOR, ACCOUNTANT |
| Dashboard operational counts | ADMIN only |
| Patients | ADMIN, RECEPTIONIST, DOCTOR |
| Doctors | ADMIN, RECEPTIONIST |
| Appointments | ADMIN, RECEPTIONIST, DOCTOR |
| Admissions | ADMIN, RECEPTIONIST |
| Rooms | ADMIN, RECEPTIONIST |
| Beds | ADMIN, RECEPTIONIST |
| Billing and payments | ADMIN, ACCOUNTANT |
| Reports | ADMIN, ACCOUNTANT (policy entry; no report service/DAO workflow exists yet) |
| User management | ADMIN |

Admissions, rooms, and beds use the Receptionist role because those areas
support registration and placement workflows. Dashboard counts are restricted
to administrators even though the dashboard shell is reachable by all roles.
Each service call re-reads the current session; a caller-supplied role or
screen visibility cannot grant access.

Self-registration remains available before sign-in. `UserService` fixes new
accounts to the Receptionist role and Pending status; an administrator must
review them before authentication is possible.

Notifications and report generation have no operational service/DAO
implementation yet. Billing currently owns bill and payment DAO operations.
