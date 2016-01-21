package be.hepl.benbear.accounting_db;

import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

public class Bonus {

    @PrimaryKey
    private final int bonusId;
    private final float amount;
    private final Date bonusDate;
    private final String reason;
    private final String source;
    private final char paid;

    public Bonus(int bonusId, float amount, Date bonusDate, String reason, String source, char paid) {
        this.bonusId = bonusId;
        this.amount = amount;
        this.bonusDate = bonusDate;
        this.reason = reason;
        this.source = source;
        this.paid = paid;
    }

    public int getBonusId() {
        return bonusId;
    }

    public float getAmount() {
        return amount;
    }

    public Date getBonusDate() {
        return bonusDate;
    }

    public String getReason() {
        return reason;
    }

    public String getSource() {
        return source;
    }

    public boolean isPaid() {
        return paid != '0';
    }
}
