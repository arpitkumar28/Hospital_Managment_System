package ui;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BillingPanelTest {
    @Test
    void paymentConfirmationUsesRecordedAmountWithoutInferringCurrentBalance() {
        assertEquals("Payment of ₹25.00 was recorded for Bill ID 17.",
                BillingPanel.paymentRecordedMessage(17, new BigDecimal("25.00"), true));
    }

    @Test
    void refreshFailureStillClearlyConfirmsPaymentWasRecorded() {
        String message = BillingPanel.paymentRecordedMessage(17, new BigDecimal("25.00"), false);

        assertTrue(message.startsWith("Payment of ₹25.00 was recorded for Bill ID 17."));
        assertTrue(message.contains("bill list could not be refreshed"));
        assertFalse(message.contains("Payment failed"));
    }
}
