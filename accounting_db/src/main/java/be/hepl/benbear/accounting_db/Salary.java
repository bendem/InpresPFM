package be.hepl.benbear.accounting_db;

import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

public class Salary {

    @PrimaryKey
    private final int salaryId;
    private final int staffId;
    private final Date dueDate;
    private final float amount;
    private final float onssFee;
    private final float deduction;
    private final char sent;
    private final char paid;

    public Salary(int salaryId, int staffId, Date dueDate, float amount, float onssFee, float deduction, char sent, char paid) {
        this.salaryId = salaryId;
        this.staffId = staffId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.onssFee = onssFee;
        this.deduction = deduction;
        this.sent = sent;
        this.paid = paid;
    }

    public int getSalaryId() {
        return salaryId;
    }

    public int getStaffId() {
        return staffId;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public float getAmount() {
        return amount;
    }

    public float getOnssFee() {
        return onssFee;
    }

    public float getDeduction() {
        return deduction;
    }

    public boolean isSent() {
        return sent != '0';
    }

    public boolean isPaid() {
        return paid != '0';
    }
}
