package ui;

import model.BillingReport;
import model.BillingReport.PaymentStatusTotal;
import model.BillingReport.RevenueTotal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportsPanelTest {
    @Test
    void csvContainsSummaryAndBreakdownsFromReportResult() {
        BillingReport report = new BillingReport(LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31), 1, new BigDecimal("100.00"),
                new BigDecimal("25.00"), new BigDecimal("75.00"),
                List.of(new PaymentStatusTotal("PARTIAL", 1, new BigDecimal("100.00"),
                        new BigDecimal("25.00"), new BigDecimal("75.00"))),
                List.of(new RevenueTotal(LocalDate.of(2026, 1, 5), new BigDecimal("25.00"))),
                List.of(new RevenueTotal(LocalDate.of(2026, 1, 1), new BigDecimal("25.00"))));

        String csv = ReportsPanel.toCsv(report);

        assertTrue(csv.contains("Total billed,100.00"));
        assertTrue(csv.contains("\"PARTIAL\",1,100.00,25.00,75.00"));
        assertTrue(csv.contains("2026-01-05,25.00"));
        assertTrue(csv.contains("cumulative payments attributed to bill date"));
    }
}
