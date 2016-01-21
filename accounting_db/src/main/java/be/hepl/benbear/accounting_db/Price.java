package be.hepl.benbear.accounting_db;

import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Timestamp;

public class Price {

    @PrimaryKey
    private final int priceId;
    private final String priceType;
    private final String stuffUsed;
    private final char oil;
    private final Timestamp lastUpdate;

    public Price(int priceId, String priceType, String stuffUsed, char oil, Timestamp lastUpdate) {
        this.priceId = priceId;
        this.priceType = priceType;
        this.stuffUsed = stuffUsed;
        this.oil = oil;
        this.lastUpdate = lastUpdate;
    }

    public int getPriceId() {
        return priceId;
    }

    public String getPriceType() {
        return priceType;
    }

    public String getStuffUsed() {
        return stuffUsed;
    }

    public boolean isOil() {
        return oil != '0';
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }
}
