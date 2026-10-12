package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Financial report totals for bills dated within the requested inclusive range. */
public record BillingReport(
        LocalDate from,
        LocalDate to,
        long billCount,
        BigDecimal billedAmount,
        BigDecimal collectedAmount,
        BigDecimal outstandingAmount,
        List<PaymentStatusTotal> paymentStatusTotals,
        List<RevenueTotal> dailyRevenue,
        List<RevenueTotal> monthlyRevenue) {

    public BillingReport {
        paymentStatusTotals = List.copyOf(paymentStatusTotals);
        dailyRevenue = List.copyOf(dailyRevenue);
        monthlyRevenue = List.copyOf(monthlyRevenue);
    }

    public record PaymentStatusTotal(
            String status,
            long billCount,
            BigDecimal billedAmount,
            BigDecimal collectedAmount,
            BigDecimal outstandingAmount) { }

    public record RevenueTotal(LocalDate period, BigDecimal collectedAmount) { }
}
