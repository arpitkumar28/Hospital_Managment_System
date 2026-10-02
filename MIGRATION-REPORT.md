# Phase 1 Migration Report

Inspection date: 2026-10-01

## 1. Existing file structure

```text
HospitalManagementSystem/
├── src/
│   ├── Main.java
│   ├── database/DatabaseConnection.java
│   ├── dao/{Admission,Appointment,Bed,Bill,Dashboard,Doctor,Patient,Room,User}DAO.java
│   └── ui/{AdminDashboard,AdmissionPanel,AppointmentPanel,BedPanel,BillingPanel,
│           DoctorPanel,LoginFrame,PatientPanel,RoomPanel}.java
├── .classpath
├── .project
├── .settings/{org.eclipse.jdt.core.prefs,org.eclipse.core.resources.prefs}
├── README.md
├── .gitignore
└── bin/ (ignored compiled output)
```

No SQL scripts, module descriptor, Maven descriptor, test sources, or source files under `model`/`service` were found. The `bin/model` and `bin/service` directories contain no tracked classes; compiled output is not authoritative source.

## 2. Existing packages

- Default package: `Main`
- `database`
- `dao`
- `ui`

## 3. Existing classes

- Entry point: `Main`
- Database: `DatabaseConnection`
- DAOs: `AdmissionDAO`, `AppointmentDAO`, `BedDAO`, `BillDAO`, `DashboardDAO`, `DoctorDAO`, `PatientDAO`, `RoomDAO`, `UserDAO`
- Swing UI: `AdminDashboard`, `AdmissionPanel`, `AppointmentPanel`, `BedPanel`, `BillingPanel`, `DoctorPanel`, `LoginFrame`, `PatientPanel`, `RoomPanel`

## 4. Existing database tables

There is no schema or migration SQL in the repository. SQL references indicate these tables: `users`, `patients`, `doctors`, `rooms`, `beds`, `admissions`, `appointments`, and `bills`. Query references imply keys including `patient_id`, `doctor_id`, `room_id`, `bed_id`, `admission_id`, `appointment_id`, and `bill_id`. Relationships and exact column definitions must be verified against the actual database before writing scripts or changing SQL.

## 5. Existing dependencies

- Java standard library, including Swing and JDBC
- MySQL Connector/J via an absolute local JAR path in `.classpath` (`mysql-connector-j-26.7.0.jar`)
- No other dependency declarations found
- `.classpath` targets JavaSE-21. No `module-info.java` exists.

The local Connector/J JAR path is not portable. The new `pom.xml` replaces it with Maven-managed Connector/J. JUnit 5 and SLF4J dependencies are declared for later phases; they are not yet used by source code. BCrypt is deferred until authentication compatibility is investigated.

## 6. Classes that can be reused

All 19 Java source classes can be retained as the functional baseline. DAOs already use JDBC `PreparedStatement` for the examined CRUD/query operations. `AdmissionDAO` already includes transaction handling for admission/discharge operations. `DatabaseConnection` already reads `HOSPITAL_DB_PASSWORD` and does not log it.

## 7. Classes that need refactoring

- `UserDAO` matches the supplied password directly in SQL; password hashing is needed before enforcing BCrypt.
- `BillDAO`, `DoctorDAO`, and `RoomDAO` use `double` for monetary values.
- `AppointmentPanel` performs patient/doctor SQL directly in the UI.
- Most UI classes call DAOs directly, so business logic is not separated into services.
- DB connection URL/port are fixed, and error handling in source should be reviewed as code is migrated.
- Existing packages differ from the target `com.hospital.*` hierarchy; package migration should be staged to avoid breaking the working UI.

## 8. Classes that should not be changed in Phase 1

All existing application classes and Eclipse metadata are preserved. There is no evidence that Eclipse configuration can safely be removed yet; the new Maven build is additive and uses the existing `src` layout.

## 9. Potential compilation problems

- Maven is not installed/available on the current PATH, so `mvn clean compile` cannot run in this environment.
- The current Java runtime is Java 25; the project is configured for Java 17 source/API compatibility in Maven.
- Some APIs used by the project may need adjustments when actually compiled against Java 17; this remains unverified until Maven is available.
- The Eclipse project currently references Java 21 and a machine-specific external Connector/J JAR.

## 10. Potential database problems

- Exact database schema, constraints, SQL types, and initial data cannot be inspected because no SQL dump/schema is in the repository and no database introspection was performed.
- Authentication appears to expect plain-text password equality; existing stored password format is unknown.
- Financial values are represented as `double` in existing persistence/UI flows.
- Admission/discharge transactions exist in `AdmissionDAO`; payment changes and bill updates need transaction/business-rule review in a later phase.
- The existing database and application database behavior have not been modified.

## Phase 1 changes

- Created `backup/phase-1-pre-maven/tracked-project.tar` from all Git-tracked project files before changes. The backup is under the ignored `backup/` directory.
- Added a Maven descriptor that retains the current `src` layout, targets Java 17, and manages Connector/J, JUnit 5, and SLF4J dependencies.
- Added this inspection report.
- No Java source, database schema, or Eclipse configuration was changed.
