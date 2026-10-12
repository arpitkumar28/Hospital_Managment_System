# Billing module

## Existing functionality

The Swing Billing screen uses `BillingService` for bill creation, editing,
deletion, payment recording, balance lookup, and bill listing. The service
requires an authenticated `ADMIN` or `ACCOUNTANT` session before it calls
`BillDAO`. The DAO persists to the existing PostgreSQL `bills` table.

Bill totals are the sum of room, doctor, medicine, and other charges. Payment
status is `PENDING` when nothing has been paid, `PARTIAL` when a positive
balance remains, and `PAID` when paid equals the total. Additional payments
must be positive and may not exceed the outstanding balance. Recording a
payment updates the paid amount and status in one conditional SQL update, so
concurrent payments cannot overwrite one another or overpay a bill.

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

## Known limitations

- Payments update the cumulative `paid_amount`; there is no separate payment
  ledger, receipt, refund, or payment-method workflow.
- The current billing form requires an admission ID even though the database
  column permits `NULL`.
- Bill history, payment reconciliation, exports, and reporting are not
  implemented here.
- Unit tests use pure calculation/validation functions and fake DAOs. They do
  not verify PostgreSQL connectivity or execute SQL against a live database.
- Billing screen database calls are synchronous on the Swing event thread.

## Tests

Run the project unit suite from the repository root:

```sh
mvn -Dmaven.repo.local=/tmp/hms-m2 clean test
```

`test/dao/BillDAOTest.java` covers total calculation, currency boundaries,
negative/null/fractional values, and unpaid/partial/paid status. The
`BillingServiceTest` and `OperationalAuthorizationTest` cover service
authorization and that denied operations do not reach the DAO. No live
database is required for these unit tests.
