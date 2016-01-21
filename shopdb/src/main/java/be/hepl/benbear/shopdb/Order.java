package be.hepl.benbear.shopdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("orders")
public class Order {

    @PrimaryKey
    private final int orderId;
    private final String username;

    public Order(int orderId, String username) {
        this.orderId = orderId;
        this.username = username;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getUsername() {
        return username;
    }
}

