# Billing module

## Existing functionality

The Swing Billing screen uses `BillingService` for bill creation, editing,
deletion, payment recording, balance lookup, and bill listing. The service
requires an authenticated `ADMIN` or `ACCOUNTANT` session before it calls
`BillDAO`. The DAO persists to the existing PostgreSQL `bills` table.

Bill totals are the sum of room, doctor, medicine, and other charges. Payment
status is `PENDING` when nothing has been paid, `PARTIAL` when a positive
balance remains, and `PAID` when paid equals the total. Additional payments
must be positive and may not exceed the outstanding balance. A payment locks
the bill row, checks the current balance, and updates the paid amount and
status in a transaction. Concurrent payments cannot overwrite one another or
overpay a bill. Bill edits lock the row, preserve the cumulative paid amount,
and reject changes to that amount; subsequent collection must use the payment
operation.

## Amount validation

- Charge components and paid amounts must be present, non-negative currency
  values with at most two decimal places.
- Individual amounts and the computed total must fit PostgreSQL
  `NUMERIC(12,2)` (maximum `9,999,999,999.99`).
- A bill may be unpaid (`paid_amount = 0`). A paid amount may not exceed the
  bill total.
- Recorded payments must be greater than zero, fit `NUMERIC(12,2)`, and fit
  within the bill's remaining balance.
- A charge total of zero is representable in the database and calculation
  layer, although the current Swing screen requires a positive total to create
  a bill.
- Patient IDs must identify an existing patient. Admission IDs are optional;
  when provided, the admission must exist and belong to the selected patient.
- Billed and paid amounts are derived from the charge components and the
  cumulative payment value; the status is recalculated from those values.

## Known limitations

- Payments update the cumulative `paid_amount`; there is no separate payment
  ledger, receipt, refund, or payment-method workflow.
- Without a payment ledger, the system cannot retain individual payment dates,
  methods, receipts, or refunds.
- Reports attribute the cumulative paid amount to the bill date. This is not
  equivalent to daily cash collections when a bill is paid after its issue
  date.
- Bill deletion remains available to authorized billing staff; the schema
  prevents deletion where dependent records exist.
- Billing database operations run in Swing background workers. Asynchronous
  operations can be exercised in the isolated PostgreSQL integration test.

## Tests

Run the project unit suite from the repository root:

```sh
mvn -Dmaven.repo.local=/tmp/hms-m2 clean test
```

`test/dao/BillDAOTest.java` covers total calculation, currency boundaries,
negative/null/fractional values, and unpaid/partial/paid status. The
`BillingServiceTest` and `OperationalAuthorizationTest` cover service
authorization and that denied operations do not reach the DAO. No database is
required for these unit tests.

`PostgresBillingIntegrationTest` is opt-in. It only runs when
`HOSPITAL_TEST_DB_NAME` has the `_test` suffix and verifies the connected
database name exactly matches it. Create a dedicated test database, apply
`database/migrations/001_initial_schema.sql`, then set the existing
`HOSPITAL_DB_HOST`, `HOSPITAL_DB_NAME`, `HOSPITAL_DB_USER`, and
`HOSPITAL_DB_PASSWORD` variables to that test database and set
`HOSPITAL_TEST_DB_NAME` to its exact name. Run:

```sh
mvn -Dmaven.repo.local=/tmp/hms-m2 -Dtest=PostgresBillingIntegrationTest clean test
```

Never point the integration test at a development or production database.
