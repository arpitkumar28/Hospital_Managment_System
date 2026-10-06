package service;

import dao.BillDAO;
import security.AuthorizationService;
import security.SessionManager;

import java.util.List;

/** Session-authorized billing and payment operations. */
public final class BillingService extends AuthorizedService {
    private final BillDAO bills;

    public BillingService() { this(new BillDAO()); }
    public BillingService(BillDAO bills) { this(bills, SessionManager.INSTANCE, new AuthorizationService()); }
    public BillingService(BillDAO bills, SessionManager sessions, AuthorizationService authorization) {
        super(sessions, authorization);
        this.bills = bills;
    }

    public boolean addBill(int patientId, int admissionId, double roomCharges, double doctorCharges,
                           double medicineCharges, double otherCharges, double paidAmount) {
        return execute(AuthorizationService.BILLING, () -> bills.addBill(patientId, admissionId, roomCharges,
                doctorCharges, medicineCharges, otherCharges, paidAmount));
    }
    public boolean updateBill(int billId, int patientId, int admissionId, double roomCharges, double doctorCharges,
                              double medicineCharges, double otherCharges, double paidAmount) {
        return execute(AuthorizationService.BILLING, () -> bills.updateBill(billId, patientId, admissionId,
                roomCharges, doctorCharges, medicineCharges, otherCharges, paidAmount));
    }
    public boolean deleteBill(int billId) { return execute(AuthorizationService.BILLING, () -> bills.deleteBill(billId)); }
    public List<Object[]> getAllBills() { return execute(AuthorizationService.BILLING, bills::getAllBills); }
    public List<Object[]> getBillsByPatient(int patientId) {
        return execute(AuthorizationService.BILLING, () -> bills.getBillsByPatient(patientId));
    }
    public Object[] getBillById(int billId) { return execute(AuthorizationService.BILLING, () -> bills.getBillById(billId)); }
    public boolean addPayment(int billId, double paymentAmount) {
        return execute(AuthorizationService.BILLING, () -> bills.addPayment(billId, paymentAmount));
    }
    public double getRemainingAmount(int billId) {
        return execute(AuthorizationService.BILLING, () -> bills.getRemainingAmount(billId));
    }
    public double calculateTotal(double roomCharges, double doctorCharges, double medicineCharges, double otherCharges) {
        return execute(AuthorizationService.BILLING,
                () -> bills.calculateTotal(roomCharges, doctorCharges, medicineCharges, otherCharges));
    }
}
