package dao;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BillDAOTest {
    private final BillDAO bills = new BillDAO();

    @Test
    void calculateTotalAddsCurrencyAmountsExactly() {
        BigDecimal total = bills.calculateTotal(
                new BigDecimal("10.10"),
                new BigDecimal("20.20"),
                new BigDecimal("0.50"),
                new BigDecimal("1.20"));

        assertEquals(new BigDecimal("32.00"), total);
    }

    @Test
    void calculateTotalAllowsZeroCharges() {
        assertEquals(new BigDecimal("0.00"), bills.calculateTotal(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    void calculateTotalRejectsNullNegativeAndFractionsOfACent() {
        assertThrows(IllegalArgumentException.class,
                () -> bills.calculateTotal(null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> bills.calculateTotal(new BigDecimal("-0.01"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> bills.calculateTotal(new BigDecimal("1.001"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    void calculateTotalRejectsAmountsOutsideNumericTwelveTwoRange() {
        assertThrows(IllegalArgumentException.class,
                () -> bills.calculateTotal(new BigDecimal("10000000000.00"),
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> bills.calculateTotal(new BigDecimal("6000000000.00"),
                        new BigDecimal("6000000000.00"), BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    void zeroPaidAmountIsValidForAnUnpaidBill() {
        assertTrue(BillDAO.isValidPaidAmount(BigDecimal.ZERO, new BigDecimal("100.00")));
        assertEquals("PENDING", BillDAO.getPaymentStatus(new BigDecimal("100.00"), BigDecimal.ZERO));
    }

    @Test
    void paidAmountMustBePresentNonNegativeWithinTotalAndCurrencyRange() {
        BigDecimal total = new BigDecimal("100.00");
        assertFalse(BillDAO.isValidPaidAmount(null, total));
        assertFalse(BillDAO.isValidPaidAmount(new BigDecimal("-0.01"), total));
        assertFalse(BillDAO.isValidPaidAmount(new BigDecimal("100.01"), total));
        assertFalse(BillDAO.isValidPaidAmount(new BigDecimal("10000000000.00"),
                new BigDecimal("10000000000.00")));
        assertTrue(BillDAO.isValidPaidAmount(new BigDecimal("1.000"), total));
    }

    @Test
    void paymentMustBePositiveCurrencyWithinDatabaseAmountRange() {
        assertFalse(BillDAO.isValidPaymentAmount(null));
        assertFalse(BillDAO.isValidPaymentAmount(BigDecimal.ZERO));
        assertFalse(BillDAO.isValidPaymentAmount(new BigDecimal("-0.01")));
        assertFalse(BillDAO.isValidPaymentAmount(new BigDecimal("0.001")));
        assertFalse(BillDAO.isValidPaymentAmount(new BigDecimal("10000000000.00")));
        assertTrue(BillDAO.isValidPaymentAmount(new BigDecimal("9999999999.99")));
        assertTrue(BillDAO.isValidPaymentAmount(new BigDecimal("1.000")));
    }

    @Test
    void paymentCannotExceedOutstandingBalance() {
        BigDecimal total = new BigDecimal("100.00");
        BigDecimal paid = new BigDecimal("60.00");

        assertTrue(BillDAO.isPaymentWithinBalance(new BigDecimal("40.00"), total, paid));
        assertFalse(BillDAO.isPaymentWithinBalance(new BigDecimal("40.01"), total, paid));
        assertFalse(BillDAO.isPaymentWithinBalance(BigDecimal.ZERO, total, paid));
    }

    @Test
    void paymentStatusTracksUnpaidPartialAndFullyPaidAmounts() {
        BigDecimal total = new BigDecimal("100.00");

        assertEquals("PENDING", BillDAO.getPaymentStatus(total, new BigDecimal("0.00")));
        assertEquals("PARTIAL", BillDAO.getPaymentStatus(total, new BigDecimal("45.25")));
        assertEquals("PAID", BillDAO.getPaymentStatus(total, total));
    }
}
