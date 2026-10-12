# Financial reports

The Reports module is available to authenticated `ADMIN` and `ACCOUNTANT`
users through `ReportService`. It queries the existing PostgreSQL `bills`
table and does not create or depend on a new table.

## Date and metric semantics

The selected start and end dates are inclusive and filter on `bill_date`.
Summary figures are:

- **Total billed:** sum of `total_amount` for bills in the range.
- **Collected:** sum of each bill's cumulative `paid_amount`.
- **Outstanding:** sum of `total_amount - paid_amount`.
- **Payment status:** bill counts and the same billed/collected/outstanding
  aggregates grouped by the stored status.
- **Daily/monthly collected:** cumulative `paid_amount` grouped by the bill
  date or bill month.

The existing schema has no payment event ledger or payment timestamp. As a
result, collected values are attributed to the bill date; they are not a
historical series of the dates cash was actually received. Summary, status,
and trend queries run in a repeatable-read transaction to keep one report
internally consistent. Separate aggregations avoid multiplying sums through
one-to-many joins.

CSV export contains the currently loaded database result and carries the same
bill-date attribution note.

## Verification

The ordinary unit suite uses a fake report DAO and does not need PostgreSQL:

```sh
mvn -Dmaven.repo.local=/tmp/hms-m2 clean test
```

The billing report SQL is also exercised by the opt-in
`PostgresBillingIntegrationTest`. Follow the isolated test database setup in
[`billing.md`](./billing.md); do not aim it at a development or production
database.
