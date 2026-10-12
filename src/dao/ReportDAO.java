package dao;

import database.DatabaseConnection;
import model.BillingReport;
import model.BillingReport.PaymentStatusTotal;
import model.BillingReport.RevenueTotal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** PostgreSQL reports over the existing cumulative bill-payment model. */
public class ReportDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportDAO.class);

    public BillingReport getBillingReport(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("Choose a valid inclusive date range.");
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            try {
                Object[] summary = getSummary(connection, from, to);
                BillingReport report = new BillingReport(from, to,
                        (long) summary[0],
                        (BigDecimal) summary[1],
                        (BigDecimal) summary[2],
                        (BigDecimal) summary[3],
                        getStatusTotals(connection, from, to),
                        getRevenue(connection, from, to, false),
                        getRevenue(connection, from, to, true));
                connection.commit();
                return report;
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            LOGGER.error("Unable to generate billing report for {} through {}.", from, to, exception);
            throw new IllegalStateException("Unable to generate the billing report.", exception);
        }
    }

    private Object[] getSummary(Connection connection, LocalDate from, LocalDate to) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS bill_count,
                       COALESCE(SUM(total_amount), 0) AS billed_amount,
                       COALESCE(SUM(paid_amount), 0) AS collected_amount,
                       COALESCE(SUM(total_amount - paid_amount), 0) AS outstanding_amount
                FROM bills
                WHERE bill_date >= CAST(? AS DATE)
                  AND bill_date < (CAST(? AS DATE) + INTERVAL '1 day')
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindRange(statement, from, to);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) throw new SQLException("Billing summary query returned no row.");
                return new Object[]{result.getLong("bill_count"), result.getBigDecimal("billed_amount"),
                        result.getBigDecimal("collected_amount"), result.getBigDecimal("outstanding_amount")};
            }
        }
    }

    private List<PaymentStatusTotal> getStatusTotals(Connection connection, LocalDate from, LocalDate to)
            throws SQLException {
        String sql = """
                SELECT payment_status, COUNT(*) AS bill_count,
                       COALESCE(SUM(total_amount), 0) AS billed_amount,
                       COALESCE(SUM(paid_amount), 0) AS collected_amount,
                       COALESCE(SUM(total_amount - paid_amount), 0) AS outstanding_amount
                FROM bills
                WHERE bill_date >= CAST(? AS DATE)
                  AND bill_date < (CAST(? AS DATE) + INTERVAL '1 day')
                GROUP BY payment_status
                ORDER BY payment_status
                """;
        List<PaymentStatusTotal> totals = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindRange(statement, from, to);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    totals.add(new PaymentStatusTotal(result.getString("payment_status"),
                            result.getLong("bill_count"),
                            result.getBigDecimal("billed_amount"), result.getBigDecimal("collected_amount"),
                            result.getBigDecimal("outstanding_amount")));
                }
            }
        }
        return totals;
    }

    private List<RevenueTotal> getRevenue(Connection connection, LocalDate from, LocalDate to, boolean monthly)
            throws SQLException {
        String period = monthly ? "date_trunc('month', bill_date)::date" : "bill_date::date";
        String sql = "SELECT " + period + " AS period, COALESCE(SUM(paid_amount), 0) AS collected_amount "
                + "FROM bills WHERE bill_date >= CAST(? AS DATE) "
                + "AND bill_date < (CAST(? AS DATE) + INTERVAL '1 day') "
                + "GROUP BY period ORDER BY period";
        List<RevenueTotal> totals = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindRange(statement, from, to);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Date date = result.getDate("period");
                    totals.add(new RevenueTotal(date.toLocalDate(), result.getBigDecimal("collected_amount")));
                }
            }
        }
        return totals;
    }

    private void bindRange(PreparedStatement statement, LocalDate from, LocalDate to) throws SQLException {
        statement.setDate(1, Date.valueOf(from));
        statement.setDate(2, Date.valueOf(to));
    }

    private void rollback(Connection connection, Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            original.addSuppressed(rollbackFailure);
        }
    }
}
