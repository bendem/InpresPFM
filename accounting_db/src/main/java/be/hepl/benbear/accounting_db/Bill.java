package be.hepl.benbear.accounting_db;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("bills")
public class Bill {

    @PrimaryKey
    private final int billId;
    private final int companyId;
    private final Date billDate;
    private final float totalPriceExcludingVat;
    private final float totalPriceIncludingVat;
    private final char validated;
    private final String accountantValidater;
    private final char sent;
    private final String billSupport;
    private final char paid;

    public Bill(int billId, int companyId, Date billDate, float totalPriceExcludingVat, float totalPriceIncludingVat, char validated, String accountantValidater, char sent, String billSupport, char paid) {
        this.billId = billId;
        this.companyId = companyId;
        this.billDate = billDate;
        this.totalPriceExcludingVat = totalPriceExcludingVat;
        this.totalPriceIncludingVat = totalPriceIncludingVat;
        this.validated = validated;
        this.accountantValidater = accountantValidater;
        this.sent = sent;
        this.billSupport = billSupport;
        this.paid = paid;
    }

    public int getBillId() {
        return billId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public Date getBillDate() {
        return billDate;
    }

    public float getTotalPriceExcludingVat() {
        return totalPriceExcludingVat;
    }

    public float getTotalPriceIncludingVat() {
        return totalPriceIncludingVat;
    }

    public boolean isValidated() {
        return validated != '0';
    }

    public String getAccountantValidater() {
        return accountantValidater;
    }

    public boolean isSent() {
        return sent != '0';
    }

    public String getBillSupport() {
        return billSupport;
    }

    public boolean isPaid() {
        return paid != '0';
    }

}
