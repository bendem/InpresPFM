package be.hepl.benbear.shopdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

@DBTable("items")
public class Item {

    @PrimaryKey
    private final int itemId;
    private final String name;
    private final double price;
    private final int stock;

    public Item(int itemId, String name, double price, int stock) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
}

